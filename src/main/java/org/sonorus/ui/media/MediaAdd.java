/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.media;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.hedwig.cloud.response.HedwigResponseCode;
import org.hedwig.cloud.response.HedwigResponseMessage;
import org.leviosa.core.client.MediaClient;
import org.leviosa.core.client.TermInstanceClient;
import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.leviosa.constants.CMSConstants;
import org.hedwig.cms.dto.MediaDTO;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.hedwig.leviosa.constants.MediaMeta;
import org.hedwig.cms.dto.TermMetaDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.extensions.model.fluidgrid.FluidGridItem;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author bhaduri
 */
@Named(value = "mediaAdd")
@ViewScoped
public class MediaAdd implements Serializable {

    private String termSlug;
    private String termName;
    private String tempFilePath;
    private boolean fileUploaded;
    private List<FluidGridItem> formItems;
    Map<String, Object> screenTermInstance;
    List<Map<String, Object>> termScreenFields;

    /**
     * Creates a new instance of TermInstanceAdd
     */
    public MediaAdd() {
        fileUploaded = false;
    }

    public String getTermSlug() {
        return termSlug;
    }

    public void setTermSlug(String termSlug) {
        this.termSlug = termSlug;
    }

    public void creteTermForm() {
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());
        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        formItems = new ArrayList<>();
        TermMetaDTO termMetaDTO = new TermMetaDTO();
        termMetaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termMetaDTO.setTermSlug(termSlug);
        termMetaDTO = mts.getTermMetaList(termMetaDTO);
        termScreenFields = termMetaDTO.getTermMetaFields();

//        for (Map<String, Object> termScreenField : termScreenFields) {
//            Map<String, String> selectTermList = (Map<String, String>) termScreenField.get("selectTermList");
//            List<SelectItem> selectItems;
//            if (selectTermList != null) {
//                selectItems = selectTermList.entrySet().stream().map(selectTerm -> {
//                    SelectItem selectItem = new SelectItem(selectTerm.getKey(), selectTerm.getValue());
//                    return selectItem;
//                }).collect(Collectors.toList());
//            } else {
//                selectItems = null;
//            }
//            //List<SelectItem> selectItems = TermUtil.convertMapToSelectItem(termMetaBean.getSelectTermList());
//            FormField formField = new FormField((String) termScreenField.get("description"), null, (String) termScreenField.get("metaKey"), (Boolean) termScreenField.get("mandatory"), (Boolean) termScreenField.get("disableOnScreen"), selectItems);
//            FluidGridItem formFieldItem = new FluidGridItem(formField, (String) termScreenField.get("dataType"));
//            formItems.add(formFieldItem);
//        }
        TermInstanceClient termInstanceClient = new TermInstanceClient();
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
        termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);

        termInstanceDTO = termInstanceClient.createMediaTermInstance(termInstanceDTO);
        screenTermInstance = termInstanceDTO.getTermInstance();
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        String fileName  = uploadedFile.getFileName();
        screenTermInstance.put(MediaMeta.MEDIA_FILE_NAME, fileName);
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        byte[] bytes = null;

        if (null != uploadedFile) {
            bytes = uploadedFile.getContents();
            String tempPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");

            tempFilePath = tempPath + "temp" + screenTermInstance.get("user") + fileExtension;
            
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
                Logger.getLogger(MediaAdd.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MediaAdd.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public String addTermInstance() {
        FacesMessage message;
        MediaClient mediaUploadClient = new MediaClient();
        if (!fileUploaded) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "File is mandatory.", "File is mandatory.");
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl =  "MediaAdd";
            return redirectUrl;
        }
        
        MediaDTO mediaDTO = new MediaDTO();
        
        mediaDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        mediaDTO.setMediaFilePath(tempFilePath);
        mediaDTO.setMediaTermInstance(screenTermInstance);
        mediaDTO = mediaUploadClient.uploadMedia(mediaDTO);
        
        if (mediaDTO.getResponseCode() == HedwigResponseCode.SUCCESS) {
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(mediaDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            //String redirectUrl = "/DataSeries/DataSeriesList?faces-redirect=true&termslug=" + termSlug;
            return "MediaList";
        } else {
            HedwigResponseMessage responseMessage = new HedwigResponseMessage();
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Data Error", responseMessage.getResponseMessage(mediaDTO.getResponseCode()));
            FacesContext f = FacesContext.getCurrentInstance();
            f.getExternalContext().getFlash().setKeepMessages(true);
            f.addMessage(null, message);
            String redirectUrl = "MediaAdd";
            return redirectUrl;
        }

    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public List<FluidGridItem> getFormItems() {
        return formItems;
    }

    public void setFormItems(List<FluidGridItem> formItems) {
        this.formItems = formItems;
    }

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }

}
