/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.media;

import java.io.Serializable;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.leviosa.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.leviosa.core.driver.LeviosaClientService;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author dgrf-iv
 */
@Named(value = "mediaEdit")
@ViewScoped
public class MediaEdit implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    
    private Map<String, Object> screenTermInstance;
    Map<String, String> termScreenFieldLabels;

    public MediaEdit() {
    }

    public void createMetaDataEditForm() {
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());

        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String)termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        //Get screen data
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        termInstanceDTO = mts.getTermInstance(termInstanceDTO);
        screenTermInstance = termInstanceDTO.getTermInstance();
        
        //Get screen fields
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        //termScreenFields = termMetaDTO.getTermMetaFields();
        termScreenFieldLabels = termMetaDTO.getTermMetaFieldLabels();
        
    }

    public String editTermInstance() {
        FacesMessage message;
        String termMetaKey;
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());

            
            


        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermInstance(screenTermInstance);
        termInstanceDTO = mts.saveTermInstance(termInstanceDTO);
        if (termInstanceDTO.getResponseCode() != 0) {
            String redirectUrl = "MediaEdit";
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;
        } else {
            String redirectUrl = "MediaList";
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(termInstanceDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return redirectUrl;
        }
    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public String getTermInstanceSlug() {
        return termInstanceSlug;
    }

    public void setTermInstanceSlug(String termInstanceSlug) {
        this.termInstanceSlug = termInstanceSlug;
    }

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }

    public Map<String, String> getTermScreenFieldLabels() {
        return termScreenFieldLabels;
    }

    public void setTermScreenFieldLabels(Map<String, String> termScreenFieldLabels) {
        this.termScreenFieldLabels = termScreenFieldLabels;
    }


    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

}
