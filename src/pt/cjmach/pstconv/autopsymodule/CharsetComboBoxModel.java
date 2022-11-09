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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author cmachado
 */
public class CharsetComboBoxModel extends DefaultComboBoxModel<String> {

    public CharsetComboBoxModel() {
        this(Charset.availableCharsets().keySet());
    }
    
    private CharsetComboBoxModel(Set<String> charsetNames) {
        super(charsetNames.toArray(new String[charsetNames.size()]));
        setSelectedItem(StandardCharsets.ISO_8859_1.name());
    }
    
}
