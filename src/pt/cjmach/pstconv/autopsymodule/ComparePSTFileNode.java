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

import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.NbBundle;

/**
 *
 * @author cmachado
 */
class ComparePSTFileNode extends AbstractNode {
    
    ComparePSTFileNode(List<EmailMessage> msgs) {
        super(new ComparePSTFileChildren(msgs, true));
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ComparePSTFileNode.class, "ComparePSTFileNode.name");
    }
    
    static class ComparePSTFileChildren extends Children.Keys<EmailMessage> {

        public ComparePSTFileChildren(List<EmailMessage> msgs, boolean lazy) {
            super(lazy);
            setKeys(msgs);
        }

        @Override
        protected Node[] createNodes(EmailMessage t) {
            Node[] nodes = new Node[] { new EmailMessageNode(t) };
            return nodes;
        }
        
    }
}
