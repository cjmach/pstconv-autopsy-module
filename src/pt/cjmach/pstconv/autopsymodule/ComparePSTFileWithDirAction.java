/*
 *  Copyright 2022 Carlos Machado
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package pt.cjmach.pstconv.autopsymodule;

import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;
import com.pff.PSTObject;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.mail.MessagingException;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.sleuthkit.autopsy.corecomponentinterfaces.DataResultViewer;
import org.sleuthkit.autopsy.corecomponents.DataResultTopComponent;
import org.sleuthkit.autopsy.corecomponents.DataResultViewerTable;
import org.sleuthkit.autopsy.corecomponents.TableFilterNode;
import org.sleuthkit.autopsy.coreutils.MessageNotifyUtil;
import org.sleuthkit.autopsy.datamodel.EmptyNode;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.ReadContentInputStream;
import org.sleuthkit.datamodel.TskCoreException;
import pt.cjmach.pstconv.MailMessageFormat;
import pt.cjmach.pstconv.PstConverter;

/**
 *
 * @author cmachado
 */
public class ComparePSTFileWithDirAction extends AbstractAction {

    private static final int ROOT_FOLDER_DESCRIPTOR_IDENTIFIER = 290;

    private final AbstractFile file;

    public ComparePSTFileWithDirAction(AbstractFile file) {
        super(NbBundle.getMessage(ComparePSTFileWithDirAction.class, "ComparePSTFileWithDirAction.name"));
        this.file = file;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ComparePSTFileWithDirOptionsPanel panel = new ComparePSTFileWithDirOptionsPanel();
        String title = (String) getValue(NAME);
        String[] options = {
            NbBundle.getMessage(ComparePSTFileWithDirAction.class, "ComparePSTFileWithDirOptionsPanel.buttonCompare.text"), // NOI18N
            NbBundle.getMessage(ComparePSTFileWithDirAction.class, "ComparePSTFileWithDirOptionsPanel.buttonCancel.text")}; // NOI18N
        String compareOption = options[0];
        NotifyDescriptor nd = new NotifyDescriptor(
                panel,
                title,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                options,
                compareOption);
        String selectedOption = (String) DialogDisplayer.getDefault().notify(nd);
        if (!compareOption.equals(selectedOption)) {
            // User cancelled or closed the dialog.
            return;
        }

        // Collect the user input.
        final File outputDir = new File(panel.getOutputDirectory());
        final MailMessageFormat outputFormat = MailMessageFormat.valueOf(panel.getOutputFormat());
        final String encoding = panel.getEncoding();

        final ProgressHandle ph = ProgressHandle.createHandle(
                NbBundle.getMessage(ComparePSTFileWithDirAction.class, "ComparePSTFileWithDirOptionsPanel.progressHandle.name")); // NOI18N
        RequestProcessor.Task task = RequestProcessor.getDefault().create(() -> {
            try {
                List<EmailMessage> msgs = compareWithDir(outputDir, outputFormat, encoding, ph);

                Collection<DataResultViewer> viewers = new ArrayList<>(1);
                viewers.add(new DataResultViewerTable());

                TableFilterNode tableFilterNode;
                if (msgs.isEmpty()) {
                    tableFilterNode = new TableFilterNode(new EmptyNode("No messages found"), true);
                } else {
                    ComparePSTFileNode resultNode = new ComparePSTFileNode(msgs);
                    tableFilterNode = new TableFilterNode(resultNode, enabled);
                }
                DataResultTopComponent msgsWindow = DataResultTopComponent.createInstance(
                        NbBundle.getMessage(ComparePSTFileWithDirAction.class, "ComparePSTFileWithDirAction.results.title"),
                        NbBundle.getMessage(ComparePSTFileWithDirAction.class, "ComparePSTFileWithDirAction.results.description", outputDir),
                        tableFilterNode, msgs.size(), viewers);
                msgsWindow.requestActive();
            } catch (PSTException | MessagingException | IOException ex) {
                MessageNotifyUtil.Notify.error(
                        NbBundle.getMessage(ComparePSTFileWithDirAction.class, "ComparePSTFileWithDirOptionsPanel.progressHandle.error"), // NOI18N
                        ex.getMessage());
            }
        });
        // start the task.
        task.schedule(0);
    }

    List<EmailMessage> compareWithDir(File directory, MailMessageFormat format, String encoding, ProgressHandle ph) throws MessagingException, IOException, PSTException {
        ph.start();
        ph.switchToIndeterminate();

        // Unfortunately, this is the only way I've found to set mstor system 
        // properties when running in the NetBeans Platform. It must be called 
        // from module code.
        System.setProperty("mstor.mbox.metadataStrategy", "none");
        System.setProperty("mstor.mbox.encoding", encoding);
        System.setProperty("mstor.mbox.bufferStrategy", "default");
        System.setProperty("mstor.cache.disabled", "true");

        PstConverter converter = new PstConverter();
        Set<Long> ids = converter.extractDescriptorIds(directory, format, encoding);
        int unit = 0;
        int workunits = ids.size();
        ph.switchToDeterminate(workunits);
        List<EmailMessage> msgs = new ArrayList<>(workunits);
        try (ReadContentInputStream stream = new ReadContentInputStream(file)) {
            PSTFile pstFile = new PSTFile(new PSTInputStreamContent(stream));
            for (Long id : ids) {
                PSTObject obj = PSTObject.detectAndLoadPSTObject(pstFile, id);
                if (obj instanceof PSTMessage) {
                    PSTMessage pstMsg = (PSTMessage) obj;
                    EmailMessage msg = createEmailMessage(file, pstFile, pstMsg);
                    msgs.add(msg);
                }
                ph.progress(++unit);
            }
        } finally {
            ph.finish();
        }
        return msgs;
    }

    EmailMessage createEmailMessage(AbstractFile absFile, PSTFile pstFile, PSTMessage msg) {
        String sender = getEmailMessageSender(msg.getSenderName(), msg.getSentRepresentingEmailAddress());

        EmailMessage result = new EmailMessage();
        result.setSourceName(absFile.getName());
        result.setEmailFrom(sender);
        result.setSubject(msg.getSubject());
        result.setDateReceived(msg.getMessageDeliveryTime());
        result.setTextBody(msg.getBody());
        result.setId(msg.getDescriptorNodeId());

        try {
            long parentDescriptorId = msg.getDescriptorNode().parentDescriptorIndexIdentifier;
            PSTFolder folder = (PSTFolder) PSTObject.detectAndLoadPSTObject(pstFile, parentDescriptorId);
            result.setPath(getEmailMessagePath(pstFile, folder));
        } catch (IOException | PSTException ex) {
            result.setPath("");
        }

        try {
            result.setDataSource(absFile.getDataSource().getName());
        } catch (TskCoreException ex) {
            result.setDataSource("");
        }
        return result;
    }

    private String getEmailMessagePath(PSTFile file, PSTFolder folder) throws IOException, PSTException {
        if (folder == null || folder.getDescriptorNodeId() == ROOT_FOLDER_DESCRIPTOR_IDENTIFIER) {
            return "\\";
        }
        PSTFolder parent = (PSTFolder) PSTObject.detectAndLoadPSTObject(file, folder.getDescriptorNode().parentDescriptorIndexIdentifier);
        return getEmailMessagePath(file, parent) + "\\" + folder.getDisplayName();
    }

    private String getEmailMessageSender(String name, String addr) {
        if (name.isEmpty() && addr.isEmpty()) {
            return "";
        }
        if (name.isEmpty()) {
            return addr;
        }
        if (addr.isEmpty()) {
            return name;
        }
        return name + ": " + addr;
    }
}
