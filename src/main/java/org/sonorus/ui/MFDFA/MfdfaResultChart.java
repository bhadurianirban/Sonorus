/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.MFDFA;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.leviosa.core.driver.CMSClientService;
import org.hedwig.cms.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.patronus.fractal.core.client.FractalCoreClient;
import org.patronus.fractal.core.dto.FractalDTO;
import org.patronus.fractal.core.dto.MFDFAResultDTO;
import org.patronus.fractal.termmeta.MFDFAResultsMeta;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author dgrfv
 */
@Named(value = "mfdfaResultChart")
@ViewScoped
public class MfdfaResultChart implements Serializable{

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> mfdfaResultInstance;
    private LineChartModel dataSeriesPlotModel;

    /**
     * Creates a new instance of MfdfaResultChart
     */
    public MfdfaResultChart() {
    }

    public void getMfdfaResultData() {
        CMSClientService cmscs = new CMSClientService();

        TermDTO termDTO = new TermDTO();
        termDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        termDTO.setTermSlug(termSlug);
        termDTO = cmscs.getTermDetails(termDTO);
        termName = (String) termDTO.getTermDetails().get(CMSConstants.TERM_NAME);
        
        TermInstanceDTO termInstanceDTO = new TermInstanceDTO();
termInstanceDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        
        termInstanceDTO.setTermSlug(termSlug);
        termInstanceDTO.setTermInstanceSlug(termInstanceSlug);
        termInstanceDTO = cmscs.getTermInstance(termInstanceDTO);
        
        mfdfaResultInstance = termInstanceDTO.getTermInstance();
        
        Double hurstExponent = Double.parseDouble((String) mfdfaResultInstance.get(MFDFAResultsMeta.HURST_EXPONENT));
        Double multiFractalWidth = Double.parseDouble((String) mfdfaResultInstance.get(MFDFAResultsMeta.MUILTI_FRACTAL_WIDTH));
        dataSeriesPlotModel = new LineChartModel();
        dataSeriesPlotModel.setSeriesColors("0000ff,000000");
        
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setFractalTermInstance(mfdfaResultInstance);
        FractalCoreClient ipsvgcalcClient = new FractalCoreClient();
        fractalDTO = ipsvgcalcClient.getMfdfaResults(fractalDTO);
        
        List<MFDFAResultDTO> mfdfaResultsList = fractalDTO.getMfdfaResultDTOs();
        LineChartSeries MFDFAHqDqScatter = new LineChartSeries();
        MFDFAHqDqScatter.setShowLine(false);
        //MFDFAHqDqScatter.setMarkerStyle("filledCircle', size:'3.0', color:'#ff0000");
        MFDFAHqDqScatter.setMarkerStyle("filledCircle', size:'3.0");
        MFDFAHqDqScatter.setLabel("Hq vs Dq");
        Double Hq,Dq;
        
        for (MFDFAResultDTO m: mfdfaResultsList) {
            Hq=m.getHq();
            Dq=m.getDq();
            MFDFAHqDqScatter.set(Hq, Dq);
        }
        Double maxHq,minHq,minDq;
        maxHq = mfdfaResultsList.stream().max(Comparator.comparing(m->m.getHq())).get().getHq();
        minHq = mfdfaResultsList.stream().min(Comparator.comparing(m->m.getHq())).get().getHq();
        
        minDq = mfdfaResultsList.stream().min(Comparator.comparing(m->m.getDq())).get().getDq();
        LineChartSeries MultiFractalWidth = new LineChartSeries();
        MultiFractalWidth.set(minHq, minDq);
        MultiFractalWidth.set(maxHq, minDq);
        MultiFractalWidth.setLabel("Multi Fractal width");
        
        String titleText = "Multifractal width: "+ multiFractalWidth+" Hurst Exponent: "+hurstExponent;
        Axis xAxis = dataSeriesPlotModel.getAxis(AxisType.X);
        Axis yAxis = dataSeriesPlotModel.getAxis(AxisType.Y);
        xAxis.setLabel("Hq");
        yAxis.setLabel("Dq");
        dataSeriesPlotModel.setTitle(titleText);
        dataSeriesPlotModel.setLegendPosition("s");
        dataSeriesPlotModel.addSeries(MFDFAHqDqScatter);
        dataSeriesPlotModel.addSeries(MultiFractalWidth);
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

    public LineChartModel getDataSeriesPlotModel() {
        return dataSeriesPlotModel;
    }

    public void setDataSeriesPlotModel(LineChartModel dataSeriesPlotModel) {
        this.dataSeriesPlotModel = dataSeriesPlotModel;
    }
    
}
