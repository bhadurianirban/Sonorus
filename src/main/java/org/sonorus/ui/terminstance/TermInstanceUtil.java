/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.terminstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dgrfi
 */
public class TermInstanceUtil {
    public static List<TermMetaKeyLabels> prepareMetaKeyList (List<Map<String, Object>> termScreenFields) {
        List<TermMetaKeyLabels> instanceMetaKeys = new ArrayList<>();

        for (Map<String, Object> termScreenField : termScreenFields) {
            if ((Boolean) termScreenField.get("renderOnGrid")) {
                TermMetaKeyLabels instanceColumns = new TermMetaKeyLabels();
                instanceColumns.setLabel((String) termScreenField.get("description"));
                instanceColumns.setMetaKey((String) termScreenField.get("metaKey") + "Desc");
                instanceMetaKeys.add(instanceColumns);
            }

        }
        return instanceMetaKeys;
    }
    
}
