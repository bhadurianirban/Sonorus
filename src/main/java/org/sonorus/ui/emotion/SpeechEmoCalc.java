/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.emotion;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.leviosa.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;
import org.sonorus.core.client.SonorusCoreClient;
import org.sonorus.core.dto.SonorusDTO;
import org.patronus.termmeta.DataSeriesMeta;
import org.patronus.constants.PatronusConstants;
import org.patronus.response.FractalResponseCode;
import org.patronus.response.FractalResponseMessage;


/**
 *
 * @author bhaduri
 */
@Named(value = "speechEmoCalc")
@ViewScoped
public class SpeechEmoCalc implements Serializable {

    private String termSlug;
    private String termName;
    private List<Map<String, Object>> dataSeriesList;
    private Map<String, Object> selectedDataSeries;
    private Map<String, String> dataSeriesFieldsLabel;
    private Map<String, Object> screenTermInstance;

    /**
     * Creates a new instance of MfdfaCalc
     */
    public SpeechEmoCalc() {
    }

    public void creteTermForm() {
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());
        //get term name
        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        //get dataseries field labels
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(PatronusConstants.TERM_SLUG_DATASERIES);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        dataSeriesFieldsLabel = termMetaDTO.getTermMetaFieldLabels();

        //get dataseries param field data
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termInstanceDTO.setTermSlug(PatronusConstants.TERM_SLUG_DATASERIES);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        if (termInstanceDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            List<Map<String, Object>> dataSeriesListAll = termInstanceDTO.getTermInstanceList();
            dataSeriesList = dataSeriesListAll.stream().filter(ds -> ds.get(DataSeriesMeta.DATA_SERIES_TYPE).equals(DataSeriesMeta.DATA_SERIES_UNIFORM)).collect(Collectors.toList());
        }

    }

    public String startCalc() {

        FacesMessage message;
        FractalResponseMessage responseMessage = new FractalResponseMessage();

        if (selectedDataSeries == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Data required.", "Data required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "SpeechEmoCalc";
            return redirectUrl;
        }

        String dataSeriesSlug = (String) selectedDataSeries.get(CMSConstants.TERM_INSTANCE_SLUG);

        //code for DGRFSpeech
        SonorusDTO dGRFSpeechDTO = new SonorusDTO();
        dGRFSpeechDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        dGRFSpeechDTO.setDataSeriesSlug(dataSeriesSlug);
        SonorusCoreClient dgrfscc = new SonorusCoreClient();
        dGRFSpeechDTO = dgrfscc.calculateSpeechEmotion(dGRFSpeechDTO);

        if (dGRFSpeechDTO.getResponseCode() != FractalResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error.", responseMessage.getResponseMessage(dGRFSpeechDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "SpeechEmoCalc";
            return redirectUrl;
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(FractalResponseCode.SUCCESS));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "SpeechEmoList";
            return redirectUrl;
        }
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

    public List<Map<String, Object>> getDataSeriesList() {
        return dataSeriesList;
    }

    public void setDataSeriesList(List<Map<String, Object>> dataSeriesList) {
        this.dataSeriesList = dataSeriesList;
    }

    public Map<String, Object> getSelectedDataSeries() {
        return selectedDataSeries;
    }

    public void setSelectedDataSeries(Map<String, Object> selectedDataSeries) {
        this.selectedDataSeries = selectedDataSeries;
    }

    public Map<String, String> getDataSeriesFieldsLabel() {
        return dataSeriesFieldsLabel;
    }

    public void setDataSeriesFieldsLabel(Map<String, String> dataSeriesFieldsLabel) {
        this.dataSeriesFieldsLabel = dataSeriesFieldsLabel;
    }

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }

}
