/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.emotion;

import java.io.Serializable;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.leviosa.core.driver.CMSClientService;
import org.hedwig.cms.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;


/**
 *
 * @author bhaduri
 */
@Named(value = "speechEmoResult")
@ViewScoped
public class SpeechEmoResult implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> speechEmoResultInstance;
    private Map<String, String> termScreenFieldsDesc;

    /**
     * Creates a new instance of MfdfaResult
     */
    public SpeechEmoResult() {
    }

    public void getSpeechEmoResultData() {
        CMSClientService cmscs = new CMSClientService();
        
        //get term name
        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = cmscs.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        //get mfdfa result term instance
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        termInstanceDTO = cmscs.getTermInstance(termInstanceDTO);
        speechEmoResultInstance = termInstanceDTO.getTermInstance();

        //get mfdfa field labels 
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = cmscs.getTermMetaList(termMetaDTO);
        termScreenFieldsDesc = termMetaDTO.getTermMetaFieldLabels();

    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermInstanceSlug() {
        return termInstanceSlug;
    }

    public void setTermInstanceSlug(String termInstanceSlug) {
        this.termInstanceSlug = termInstanceSlug;
    }

    public Map<String, String> getTermScreenFieldsDesc() {
        return termScreenFieldsDesc;
    }

    public void setTermScreenFieldsDesc(Map<String, String> termScreenFieldsDesc) {
        this.termScreenFieldsDesc = termScreenFieldsDesc;
    }

    public Map<String, Object> getSpeechEmoResultInstance() {
        return speechEmoResultInstance;
    }

    public void setSpeechEmoResultInstance(Map<String, Object> speechEmoResultInstance) {
        this.speechEmoResultInstance = speechEmoResultInstance;
    }

}
