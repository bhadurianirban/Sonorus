/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.browse;

import org.sonorus.ui.terminstance.TermMetaKeyLabels;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;

import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.leviosa.constants.CMSConstants;

import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "rootTermInstanceList")
@ViewScoped
public class RootTermInstanceList implements Serializable {

    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private List<TermMetaKeyLabels> instanceMetaKeys;
    private String termName;
    private Map<String, Object> selectedTermInstance;

    private String parentTermSlug;
    private String parentTermInstanceSlug;
    private String childTermSlug;
    private String childTermMetaKey;

    public RootTermInstanceList() {
    }

    @PostConstruct
    public void init() {

    }

    public void fillTermMetaData() {

        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());
        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        //Creation of grid
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        List<Map<String, Object>> termScreenFields = termMetaDTO.getTermMetaFields();

        //get instance data
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        screenTermInstanceList = termInstanceDTO.getTermInstanceList();
        metaDoesNotExistForTerm = !termScreenFields.isEmpty();
        instanceMetaKeys = new ArrayList<>();

        int gridColCount = 0;
        for (Map<String, Object> termScreenField : termScreenFields) {

            if ((Boolean) termScreenField.get("renderOnGrid")) {
                if (gridColCount < 4) {
                    TermMetaKeyLabels instanceColumns = new TermMetaKeyLabels();
                    instanceColumns.setLabel((String) termScreenField.get("description"));
                    instanceColumns.setMetaKey((String) termScreenField.get("metaKey") + "Desc");
                    instanceMetaKeys.add(instanceColumns);
                    gridColCount++;
                }
            }

        }

    }

    public String browseTermInstance() {
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        String pts = (String) selectedTermInstance.get(CMSConstants.TERM_SLUG);
        String ptis = (String) selectedTermInstance.get(CMSConstants.TERM_INSTANCE_SLUG);
        termMetaDTO.setTermSlug(pts);
        termMetaDTO = mts.getChildTermMetaList(termMetaDTO);
        List<Map<String, Object>> termMetaListInMap;
        if (termMetaDTO.getResponseCode() == HedwigResponseCode.SUCCESS) {
            termMetaListInMap = termMetaDTO.getTermMetaFields();

            if (termMetaListInMap.size() == 1) {
                Map<String, Object> selectedTermMetaObj = termMetaListInMap.get(0);
                parentTermSlug = pts;
                parentTermInstanceSlug = ptis;
                childTermSlug = (String) selectedTermMetaObj.get(CMSConstants.TERM_SLUG);
                childTermMetaKey = (String) selectedTermMetaObj.get(CMSConstants.META_KEY);
                return "ChildTermInstanceList";
            } else {
                return "ChildTermList";
            }
        } else {
            FacesMessage message;
            String redirectUrl = "ChildTermInstanceList";
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(termMetaDTO.getResponseCode()));
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

    public List<Map<String, Object>> getScreenTermInstanceList() {
        return screenTermInstanceList;
    }

    public void setScreenTermInstanceList(List<Map<String, Object>> screenTermInstanceList) {
        this.screenTermInstanceList = screenTermInstanceList;
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public List<TermMetaKeyLabels> getInstanceMetaKeys() {
        return instanceMetaKeys;
    }

    public void setInstanceMetaKeys(List<TermMetaKeyLabels> instanceMetaKeys) {
        this.instanceMetaKeys = instanceMetaKeys;
    }

    public Map<String, Object> getSelectedTermInstance() {
        return selectedTermInstance;
    }

    public void setSelectedTermInstance(Map<String, Object> selectedTermInstance) {
        this.selectedTermInstance = selectedTermInstance;
    }

    public boolean isMetaDoesNotExistForTerm() {
        return metaDoesNotExistForTerm;
    }

    public void setMetaDoesNotExistForTerm(boolean metaDoesNotExistForTerm) {
        this.metaDoesNotExistForTerm = metaDoesNotExistForTerm;
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

    public String getChildTermSlug() {
        return childTermSlug;
    }

    public void setChildTermSlug(String childTermSlug) {
        this.childTermSlug = childTermSlug;
    }

    public String getChildTermMetaKey() {
        return childTermMetaKey;
    }

    public void setChildTermMetaKey(String childTermMetaKey) {
        this.childTermMetaKey = childTermMetaKey;
    }

}
