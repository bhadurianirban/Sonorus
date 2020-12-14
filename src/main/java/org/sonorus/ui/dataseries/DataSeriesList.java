/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.dataseries;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.patronus.response.FractalResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.hedwig.leviosa.constants.CMSConstants;
import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;
import org.patronus.core.client.PatronusCoreClient;
import org.patronus.core.dto.FractalDTO;



/**
 *
 * @author bhaduri
 */
@Named(value = "dataSeriesList")
@ViewScoped
public class DataSeriesList implements Serializable {

    /**
     * Creates a new instance of DataSeriesList
     */
    private String termSlug;
    private List<Map<String, Object>> screenTermInstanceList;
    private boolean metaDoesNotExistForTerm;
    private Map<String, String> termScreenFieldsDesc;
    private String termName;
    private Map<String, Object> selectedMetaData;
    private String viewCumulative;

    public DataSeriesList() {
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
        
        termScreenFieldsDesc = termMetaDTO.getTermMetaFieldLabels();

        //Creation of grid
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        
        screenTermInstanceList = termInstanceDTO.getTermInstanceList();
        metaDoesNotExistForTerm = !termScreenFieldsDesc.isEmpty();

    }


    public String goToViewDataSeries(String viewCumulative) {
 
        setViewCumulative(viewCumulative);
        return "DataSeriesView";
    }



    public String deleteDataSeries() {
        FacesMessage message;
        PatronusCoreClient mts = new PatronusCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        String selectedTermInstanceSlug = (String) selectedMetaData.get("termInstanceSlug");
        fractalDTO.setDataSeriesSlug(selectedTermInstanceSlug);
        //delete dataseries metadata
        fractalDTO = mts.deleteDataseries(fractalDTO);
        if (fractalDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(fractalDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        } else {
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(fractalDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
        }
        int seriesId = Integer.parseInt((String) selectedMetaData.get("id"));

        return "DataSeriesList";
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

    public Map<String, String> getTermScreenFieldsDesc() {
        return termScreenFieldsDesc;
    }

    public void setTermScreenFieldsDesc(Map<String, String> termScreenFieldsDesc) {
        this.termScreenFieldsDesc = termScreenFieldsDesc;
    }

    public Map<String, Object> getSelectedMetaData() {
        return selectedMetaData;
    }

    public void setSelectedMetaData(Map<String, Object> selectedMetaData) {
        this.selectedMetaData = selectedMetaData;
    }

    public boolean isMetaDoesNotExistForTerm() {
        return metaDoesNotExistForTerm;
    }

    public void setMetaDoesNotExistForTerm(boolean metaDoesNotExistForTerm) {
        this.metaDoesNotExistForTerm = metaDoesNotExistForTerm;
    }

    public String getViewCumulative() {
        return viewCumulative;
    }

    public void setViewCumulative(String viewCumulative) {
        this.viewCumulative = viewCumulative;
    }

}
