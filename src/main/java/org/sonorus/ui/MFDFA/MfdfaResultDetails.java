/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.MFDFA;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.leviosa.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.patronus.core.client.PatronusCoreClient;
import org.patronus.core.dto.FractalDTO;
import org.patronus.core.dto.MFDFAResultDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;

/**
 *
 * @author dgrfv
 */
@Named(value = "mfdfaResultDetails")
@ViewScoped
public class MfdfaResultDetails implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> mfdfaResultInstance;
    private List<MFDFAResultDTO> mfdfaResultsList;

    /**
     * Creates a new instance of MfdfaResultDetails
     */
    public MfdfaResultDetails() {
    }

    public void getMfdfaResultData() {
        LeviosaClientService mts = new LeviosaClientService(CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServer(),CMSClientAuthCredentialValue.AUTH_CREDENTIALS.getHedwigServerPort());

        TermDTO termDTO = new TermDTO();
        termDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = mts.getTermDetails(termDTO);

        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);

        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
termInstanceDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);

        termInstanceDTO = mts.getTermInstance(termInstanceDTO);

        mfdfaResultInstance = termInstanceDTO.getTermInstance();

        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setFractalTermInstance(mfdfaResultInstance);
        PatronusCoreClient fractalCoreClient = new PatronusCoreClient();
        
        fractalDTO = fractalCoreClient.getMfdfaResults(fractalDTO);
        mfdfaResultsList = fractalDTO.getMfdfaResultDTOs();
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

    public Map<String, Object> getMfdfaResultInstance() {
        return mfdfaResultInstance;
    }

    public void setMfdfaResultInstance(Map<String, Object> mfdfaResultInstance) {
        this.mfdfaResultInstance = mfdfaResultInstance;
    }

    public List<MFDFAResultDTO> getMfdfaResultsList() {
        return mfdfaResultsList;
    }

    public void setMfdfaResultsList(List<MFDFAResultDTO> mfdfaResultsList) {
        this.mfdfaResultsList = mfdfaResultsList;
    }

}
