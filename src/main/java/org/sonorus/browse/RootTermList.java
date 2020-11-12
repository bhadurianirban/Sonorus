/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.browse;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.leviosa.core.client.TermClient;
import org.hedwig.cms.dto.TermDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "rootTermList")
@ViewScoped
public class RootTermList implements Serializable {

    private List<Map<String, Object>> termListInMap;
    private Map<String, String> selectedTerm;

    /**
     * Creates a new instance of RootTermList
     */
    public RootTermList() {
    }

    public void fillTermList() {
        TermClient mts = new TermClient();
        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO = mts.getRootTermList(termDTO);
        if (termDTO.getResponseCode() == HedwigResponseCode.SUCCESS) {
            termListInMap = termDTO.getTermList();
        }
    }

    public List<Map<String, Object>> getTermListInMap() {
        return termListInMap;
    }

    public void setTermListInMap(List<Map<String, Object>> termListInMap) {
        this.termListInMap = termListInMap;
    }

    public Map<String, String> getSelectedTerm() {
        return selectedTerm;
    }

    public void setSelectedTerm(Map<String, String> selectedTerm) {
        this.selectedTerm = selectedTerm;
    }
    
}
