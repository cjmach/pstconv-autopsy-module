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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.Action;
import org.openide.util.Utilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.corecomponentinterfaces.ContextMenuActionsProvider;
import org.sleuthkit.datamodel.AbstractFile;

/**
 *
 * @author cmachado
 */
@ServiceProvider(service = ContextMenuActionsProvider.class)
public class PSTFileContextMenuActionsProvider implements ContextMenuActionsProvider {
    /**
     * 
     * @return 
     */
    @Override
    public List<Action> getActions() {
        Lookup lkp = Utilities.actionsGlobalContext();
        Collection<? extends AbstractFile> nodes = lkp.lookupAll(AbstractFile.class);
        if (nodes.size() != 1) { // Only single selection is supported.
            return Collections.<Action>emptyList();
        }
        final AbstractFile file = nodes.iterator().next();
        String mimeType = file.getMIMEType();
        if (mimeType == null) {
            return Collections.<Action>emptyList();
        }
        switch (mimeType) {
            case "application/vnd.ms-outlook-pst":
            case "application/vnd.ms-outlook":
                return Arrays.asList(new ExportPSTFileAction(file), new ComparePSTFileWithDirAction(file));

            default:
                return Collections.<Action>emptyList();
        }
    }

}
