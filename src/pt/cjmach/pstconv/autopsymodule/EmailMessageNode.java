/*
 *  Copyright 2024 Carlos Machado
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

import java.text.SimpleDateFormat;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;
import org.sleuthkit.autopsy.datamodel.DisplayableItemNode;
import org.sleuthkit.autopsy.datamodel.DisplayableItemNodeVisitor;
import org.sleuthkit.autopsy.datamodel.NodeProperty;

/**
 *
 * @author cmachado
 */
public final class EmailMessageNode extends DisplayableItemNode {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    private final EmailMessage message;

    public EmailMessageNode(EmailMessage message) {
        super(Children.LEAF, Lookups.fixed(message));
        this.message = message;
        setDisplayName(message.getSourceName());
    }

    @Override
    public <T> T accept(DisplayableItemNodeVisitor<T> visitor) {
        return null;
    }

    @Override
    public boolean isLeafTypeNode() {
        return true;
    }

    @Override
    public String getItemType() {
        return EmailMessageNode.class.getName();
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set sheetSet = sheet.get(Sheet.PROPERTIES);
        
        if (sheetSet == null) {
            sheetSet = Sheet.createPropertiesSet();
            sheet.put(sheetSet);
        }
        
        NodeProperty<String> sourceNameProp = new NodeProperty<>(
                "Source Name", "Source Name", "", message.getSourceName());
        NodeProperty<String> emailFromProp = new NodeProperty<>(
                "E-mail From", "E-mail From", "", message.getEmailFrom());
        NodeProperty<String> subjectProp = new NodeProperty<>(
                "Subject", "Subject", "", message.getSubject());
        NodeProperty<String> dateReceivedProp = new NodeProperty<>(
                "Date received", "Date Received", "", DATE_FORMAT.format(message.getDateReceived()));
        NodeProperty<String> messageProp = new NodeProperty<>(
                "Message (plain text)", "Message (plain text)", "", message.getTextBody());
        NodeProperty<Long> idProp = new NodeProperty<>(
                "Message ID", "Message ID", "", message.getId());
        NodeProperty<String> pathProp = new NodeProperty<>(
                "Path", "Path", "", message.getPath());
        NodeProperty<String> dataSourceProp = new NodeProperty<>(
                "Data Source", "Data Source", "", message.getDataSource());
        
        sheetSet.put(sourceNameProp);
        sheetSet.put(emailFromProp);
        sheetSet.put(subjectProp);
        sheetSet.put(dateReceivedProp);
        sheetSet.put(messageProp);
        sheetSet.put(idProp);
        sheetSet.put(pathProp);
        sheetSet.put(dataSourceProp);
        return sheet;
    }
    
    
}
