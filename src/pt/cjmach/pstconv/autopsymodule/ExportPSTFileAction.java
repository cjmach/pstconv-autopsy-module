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
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import javax.mail.MessagingException;
import javax.swing.AbstractAction;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.RequestProcessor.Task;
import org.sleuthkit.autopsy.coreutils.MessageNotifyUtil;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.ReadContentInputStream;
import pt.cjmach.pstconv.MailMessageFormat;
import pt.cjmach.pstconv.PstConverter;

/**
 *
 * @author cmachado
 */
public class ExportPSTFileAction extends AbstractAction {

    private final AbstractFile file;

    /**
     *
     * @param file The constructor assumes this is a OST/PST file without
     * performing any checks.
     */
    public ExportPSTFileAction(AbstractFile file) {
        super(NbBundle.getMessage(ExportPSTFileAction.class, "ExportPSTFileAction.name")); // NOI18N
        this.file = file;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Create and show the Export PST File dialog.
        ExportPSTFileOptionsPanel panel = new ExportPSTFileOptionsPanel();
        String title = (String) getValue(NAME);
        String[] options = {
            NbBundle.getMessage(ExportPSTFileAction.class, "ExportPSTFileAction.buttonExport.text"), // NOI18N
            NbBundle.getMessage(ExportPSTFileAction.class, "ExportPSTFileAction.buttonCancel.text")}; // NOI18N
        String exportOption = options[0];
        NotifyDescriptor nd = new NotifyDescriptor(
                panel,
                title,
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.PLAIN_MESSAGE,
                options,
                exportOption);
        String selectedOption = (String) DialogDisplayer.getDefault().notify(nd);
        if (!exportOption.equals(selectedOption)) {
            // User cancelled or closed the dialog.
            return;
        }

        // Collect the user input.
        final File outputDir = new File(panel.getOutputDirectory());
        final MailMessageFormat outputFormat = MailMessageFormat.valueOf(panel.getOutputFormat());
        final String encoding = panel.getEncoding();

        // Create the export task.
        final ProgressHandle ph = ProgressHandle.createHandle(
                NbBundle.getMessage(ExportPSTFileAction.class, "ExportPSTFileAction.progressHandle.name")); // NOI18N
        Task task = RequestProcessor.getDefault().create(() -> {
            ph.start();
            ph.switchToIndeterminate();
            try {
                Instant start = Instant.now();
                int messageCount = convertPstFile(outputDir, outputFormat, encoding);
                Instant end = Instant.now();                
                String elapsedTime = DurationFormatUtils.formatDuration(Duration.between(start, end).toMillis(), "HH'h'mm'm'ss.SSS'S'");
                DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message(
                        NbBundle.getMessage(ExportPSTFileAction.class, "ExportPSTFileAction.progressHandle.finish", messageCount, elapsedTime))); // NOI18N
            } catch (PSTException | MessagingException | IOException ex) {
                MessageNotifyUtil.Notify.error(
                        NbBundle.getMessage(ExportPSTFileAction.class, "ExportPSTFileAction.progressHandle.step2.error"), // NOI18N
                        ex.getMessage());
            } finally {
                ph.finish();
            }
        });
        // start the export task.
        task.schedule(0);
    }

    int convertPstFile(File outputDir, MailMessageFormat outputFormat, String encoding) throws PSTException, MessagingException, IOException {
        PstConverter pstconv = new PstConverter();
        try (ReadContentInputStream stream = new ReadContentInputStream(file)) {
            PSTFile pstFile = new PSTFile(new PSTInputStreamContent(stream));
            return pstconv.convert(pstFile, outputDir, outputFormat, encoding);
        }
    }
}
