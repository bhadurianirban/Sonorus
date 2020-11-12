/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.emotion;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

import org.hedwig.cms.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;
import org.sonorus.ui.terminstance.TermInstanceUtil;
import org.sonorus.ui.terminstance.TermMetaKeyLabels;
import org.leviosa.core.driver.CMSClientService;
import org.patronus.fractal.response.FractalResponseCode;
import org.patronus.fractal.response.FractalResponseMessage;
import org.sonorus.core.client.SonorusCoreClient;
import org.sonorus.core.dto.SonorusDTO;




/**
 *
 * @author bhaduri
 */
@Named(value = "speechEmoList")
@ViewScoped
public class SpeechEmoList implements Serializable {

    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private List<TermMetaKeyLabels> instanceMetaKeys;
    private String termName;
    private Map<String, Object> selectedMetaData;

    /**
     * Creates a new instance of MfdfaList
     */
    public SpeechEmoList() {
    }

    public void fillTermMetaData() {

        CMSClientService cmscs = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = cmscs.getTermDetails(termDTO);

        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        //Creation of grid
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = cmscs.getTermMetaList(termMetaDTO);

        List<Map<String, Object>> termScreenFields = termMetaDTO.getTermMetaFields();

        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO = cmscs.getTermInstanceList(termInstanceDTO);

        screenTermInstanceList = termInstanceDTO.getTermInstanceList();

        metaDoesNotExistForTerm = !termScreenFields.isEmpty();
        instanceMetaKeys = TermInstanceUtil.prepareMetaKeyList(termScreenFields);

       

    }
    
    public String goToViewSpeechEmoresult() {
        return "SpeechEmoResult";
    }

    public String deleteMFDFAResults() {
        FacesMessage message;
        FractalResponseMessage responseMessage = new FractalResponseMessage();
        
        SonorusDTO dGRFSpeechDTO = new SonorusDTO();
        dGRFSpeechDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        dGRFSpeechDTO.setSpeechEmoTermInstance(selectedMetaData);
        
        SonorusCoreClient dgrfscc = new SonorusCoreClient();
        dGRFSpeechDTO = dgrfscc.deleteEmoInstance(dGRFSpeechDTO);
        
        if (dGRFSpeechDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(dGRFSpeechDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", responseMessage.getResponseMessage(dGRFSpeechDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        }
        return "SpeechEmoList";
    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public List<Map<String, Object>> getScreenTermInstanceList() {
        return screenTermInstanceList;
    }

    public void setScreenTermInstanceList(List<Map<String, Object>> screenTermInstanceList) {
        this.screenTermInstanceList = screenTermInstanceList;
    }

    public boolean isMetaDoesNotExistForTerm() {
        return metaDoesNotExistForTerm;
    }

    public void setMetaDoesNotExistForTerm(boolean metaDoesNotExistForTerm) {
        this.metaDoesNotExistForTerm = metaDoesNotExistForTerm;
    }

    public List<TermMetaKeyLabels> getInstanceMetaKeys() {
        return instanceMetaKeys;
    }

    public void setInstanceMetaKeys(List<TermMetaKeyLabels> instanceMetaKeys) {
        this.instanceMetaKeys = instanceMetaKeys;
    }


    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public Map<String, Object> getSelectedMetaData() {
        return selectedMetaData;
    }

    public void setSelectedMetaData(Map<String, Object> selectedMetaData) {
        this.selectedMetaData = selectedMetaData;
    }

}
