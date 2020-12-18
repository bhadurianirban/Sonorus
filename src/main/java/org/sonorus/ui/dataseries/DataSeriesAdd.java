/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.dataseries;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.patronus.response.FractalResponseCode;

import org.hedwig.leviosa.constants.CMSConstants;
import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;
import org.sonorus.core.client.SonorusCoreClient;

import org.sonorus.core.dto.SonorusDTO;
import org.patronus.core.client.PatronusCoreClient;
import org.patronus.termmeta.DataSeriesMeta;
import org.patronus.core.dto.FractalDTO;
import org.patronus.response.FractalResponseMessage;



import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;


/**
 *
 * @author dgrfv
 */
@Named(value = "dataSeriesAdd")
@ViewScoped
public class DataSeriesAdd implements Serializable {

    private String termSlug;
    private String termName;
    private String tempFilePath;
    private boolean fileUploaded;
    List<Map<String, Object>> termScreenFields;
    Map<String, Object> screenTermInstance;

    /**
     * Creates a new instance of DataSeriesAdd
     */
    public DataSeriesAdd() {
        fileUploaded = false;
    }

    public void creteTermForm() {
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());

        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);

        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        termScreenFields = termMetaDTO.getTermMetaFields();

        
        PatronusCoreClient fractalCoreClient = new  PatronusCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO = fractalCoreClient.createDataSeriesTermInstance(fractalDTO);
        screenTermInstance = fractalDTO.getFractalTermInstance();
        
    }


    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        
        String fileName  = uploadedFile.getFileName();
        screenTermInstance.put(DataSeriesMeta.DATA_SERIES_ORIGINAL_FILENAME, fileName);
        byte[] bytes = null;

        if (null != uploadedFile) {
            bytes = uploadedFile.getContent();
            String tempPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            
            tempFilePath = tempPath + "temp"+screenTermInstance.get("user")+".wav";
            
            FileOutputStream fo;

            try {
                File tempFile = new File(tempFilePath);
                fo = new FileOutputStream(tempFile);
                try (BufferedOutputStream stream = new BufferedOutputStream(fo)) {
                    stream.write(bytes);
                    stream.close();

                    fileUploaded = true;

                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(DataSeriesAdd.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DataSeriesAdd.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String addTermInstance() {

        
        
        FacesMessage message;

        if (!fileUploaded) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "File is mandatory.", "File is mandatory.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl =  "DataSeriesAdd";
            return redirectUrl;
        }
        
        //convert wav to csv and upload to dataseries
        SonorusCoreClient dgrfscc = new SonorusCoreClient();
        SonorusDTO dGRFSpeechDTO = new SonorusDTO();
        dGRFSpeechDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        dGRFSpeechDTO.setWavFilePath(tempFilePath);
        dGRFSpeechDTO.setSpeechDataSeriesTermInstance(screenTermInstance);
        dGRFSpeechDTO = dgrfscc.convertWavToCsv(dGRFSpeechDTO);
        
        
        if (dGRFSpeechDTO.getResponseCode() == FractalResponseCode.SUCCESS) {
            FractalResponseMessage responseMessage = new FractalResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(dGRFSpeechDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            return "DataSeriesList";
        } else {
            FractalResponseMessage responseMessage = new FractalResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Data Error", responseMessage.getResponseMessage(dGRFSpeechDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "DataSeriesAdd";
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

    public List<Map<String, Object>> getTermScreenFields() {
        return termScreenFields;
    }

    public void setTermScreenFields(List<Map<String, Object>> termScreenFields) {
        this.termScreenFields = termScreenFields;
    }

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }


}
