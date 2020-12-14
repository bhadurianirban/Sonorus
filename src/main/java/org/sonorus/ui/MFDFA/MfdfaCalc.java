/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.MFDFA;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.patronus.response.FractalResponseCode;
import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.leviosa.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.patronus.core.client.PatronusCoreClient;
import org.patronus.termmeta.DataSeriesMeta;
import org.patronus.constants.PatronusConstants;
import org.patronus.core.dto.FractalDTO;
import org.patronus.termmeta.MFDFAResultsMeta;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author bhaduri
 */
@Named(value = "mfdfaCalc")
@ViewScoped
public class MfdfaCalc implements Serializable {

    private String termSlug;
    private String termName;
    private List<Map<String, Object>> mfdfaParamDataList;
    private Map<String, Object> selectedmfdfaParamData;
    private List<Map<String, Object>> dataSeriesList;
    private Map<String, Object> selectedDataSeries;
    private Map<String, String> mfdfaParamFieldsLabel;
    private Map<String, String> dataSeriesFieldsLabel;
    private Map<String, Object> screenTermInstance;

    /**
     * Creates a new instance of MfdfaCalc
     */
    public MfdfaCalc() {
    }

    public void creteTermForm() {
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());
        //get term name
        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        //get mfdfa param field lables
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(PatronusConstants.TERM_SLUG_MFDFA_PARAM);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        mfdfaParamFieldsLabel = termMetaDTO.getTermMetaFieldLabels();
        //get dataseries field labels

        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(PatronusConstants.TERM_SLUG_DATASERIES);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        dataSeriesFieldsLabel = termMetaDTO.getTermMetaFieldLabels();

        //get mfdfa param field data
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);

        termInstanceDTO.setTermSlug(PatronusConstants.TERM_SLUG_MFDFA_PARAM);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        mfdfaParamDataList = termInstanceDTO.getTermInstanceList();

        //get dataseries field list
        termInstanceDTO.setTermSlug(PatronusConstants.TERM_SLUG_DATASERIES);
        termInstanceDTO = mts.getTermInstanceList(termInstanceDTO);
        if (termInstanceDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            List<Map<String, Object>> dataSeriesListAll = termInstanceDTO.getTermInstanceList();
            dataSeriesList = dataSeriesListAll.stream().filter(ds -> ds.get(DataSeriesMeta.DATA_SERIES_TYPE).equals(DataSeriesMeta.DATA_SERIES_UNIFORM)).collect(Collectors.toList());
        }

    }

    public String startCalc() {

        FacesMessage message;
        if (selectedmfdfaParamData == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Paramenter required.", "Paramenter required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "MFDFACalc";
            return redirectUrl;
        }
        if (selectedDataSeries == null) {
            message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Data required.", "Data required.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "MFDFACalc";
            return redirectUrl;
        }

        String mfdfaParamSlug = (String) selectedmfdfaParamData.get("termInstanceSlug");
        String dataSeriesSlug = (String) selectedDataSeries.get("termInstanceSlug");
        //populate PSVG results instance

        Long dataCount = Long.parseLong((String) selectedDataSeries.get(DataSeriesMeta.DATA_SERIES_LENGTH));

        PatronusCoreClient fractalCoreClient = new PatronusCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setParamSlug(mfdfaParamSlug);
        fractalDTO.setDataSeriesSlug(dataSeriesSlug);

        if (dataCount < PatronusConstants.DATA_LIMIT) {
            fractalDTO = fractalCoreClient.calculateMFDFA(fractalDTO);
            screenTermInstance = fractalDTO.getFractalTermInstance();
            if (screenTermInstance == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Something wrong.", "Contact Admin.");
                FacesContext f = FacesContext.getCurrentInstance();
                f.getExternalContext().getFlash().setKeepMessages(true);
                f.addMessage(null, message);
                String redirectUrl = "MFDFACalc";
                return redirectUrl;
            }
        } else {
            fractalDTO = fractalCoreClient.queueMFDFACalculation(fractalDTO);
            screenTermInstance = fractalDTO.getFractalTermInstance();
            if (screenTermInstance == null) {
                message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Something wrong.", "Contact Admin.");
                FacesContext f = FacesContext.getCurrentInstance();
                f.getExternalContext().getFlash().setKeepMessages(true);
                f.addMessage(null, message);
                String redirectUrl = "MFDFACalc";
                return redirectUrl;
            }
        }

        String queued = (String) screenTermInstance.get("queued");
        if (queued.equals("Yes")) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Data length is more than " + PatronusConstants.DATA_LIMIT + ". Your data is queued for processing check after some time.");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Hurst exponent:" + screenTermInstance.get(MFDFAResultsMeta.HURST_EXPONENT));
        }

        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        String redirectUrl = "MFDFACalcList";
        return redirectUrl;
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

    public List<Map<String, Object>> getMfdfaParamDataList() {
        return mfdfaParamDataList;
    }

    public void setMfdfaParamDataList(List<Map<String, Object>> mfdfaParamDataList) {
        this.mfdfaParamDataList = mfdfaParamDataList;
    }

    public Map<String, Object> getSelectedmfdfaParamData() {
        return selectedmfdfaParamData;
    }

    public void setSelectedmfdfaParamData(Map<String, Object> selectedmfdfaParamData) {
        this.selectedmfdfaParamData = selectedmfdfaParamData;
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

    public Map<String, String> getMfdfaParamFieldsLabel() {
        return mfdfaParamFieldsLabel;
    }

    public void setMfdfaParamFieldsLabel(Map<String, String> mfdfaParamFieldsLabel) {
        this.mfdfaParamFieldsLabel = mfdfaParamFieldsLabel;
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
