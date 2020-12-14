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
import org.hedwig.leviosa.constants.CMSConstants;
import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "childTermList")
@ViewScoped
public class ChildTermList implements Serializable{
    
    
    
    private String parentTermSlug;
    private String parentTermInstanceSlug;
    private String parentTermName;
    private String parentTermInstanceName;
    
    
    private List<Map<String, Object>> termMetaListInMap;
    private Map<String, String> selectedTermMeta;
//    private boolean backPress;
    /**
     * Creates a new instance of ChildTermList
     */
    public ChildTermList() {
    }
    public void fillTermList() {

        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());
        //Creation of the grid
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(parentTermSlug);
        termMetaDTO = mts.getChildTermMetaList(termMetaDTO);

        if (termMetaDTO.getResponseCode() == HedwigResponseCode.SUCCESS) {
            termMetaListInMap = termMetaDTO.getTermMetaFields();

        }
        //get the following for the header line
        //get parent term name
        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(parentTermSlug);
        termDTO = mts.getTermDetails(termDTO);
        parentTermName = (String)termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        //get parent term instance name
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(parentTermSlug);
        termInstanceDTO.setTermInstanceSlug(parentTermInstanceSlug);
        termInstanceDTO = mts.getTermInstance(termInstanceDTO);
        parentTermInstanceName = termInstanceDTO.getTermInstanceFirstField();

    }

    public String getParentTermSlug() {
        return parentTermSlug;
    }

    public void setParentTermSlug(String parentTermSlug) {
        this.parentTermSlug = parentTermSlug;
    }

    public String getParentTermInstanceSlug() {
        return parentTermInstanceSlug;
    }

    public void setParentTermInstanceSlug(String parentTermInstanceSlug) {
        this.parentTermInstanceSlug = parentTermInstanceSlug;
    }

    public List<Map<String, Object>> getTermMetaListInMap() {
        return termMetaListInMap;
    }

    public void setTermMetaListInMap(List<Map<String, Object>> termMetaListInMap) {
        this.termMetaListInMap = termMetaListInMap;
    }

    public Map<String, String> getSelectedTermMeta() {
        return selectedTermMeta;
    }

    public void setSelectedTermMeta(Map<String, String> selectedTermMeta) {
        this.selectedTermMeta = selectedTermMeta;
    }

    public String getParentTermName() {
        return parentTermName;
    }

    public void setParentTermName(String parentTermName) {
        this.parentTermName = parentTermName;
    }

    public String getParentTermInstanceName() {
        return parentTermInstanceName;
    }

    public void setParentTermInstanceName(String parentTermInstanceName) {
        this.parentTermInstanceName = parentTermInstanceName;
    }
    
}
