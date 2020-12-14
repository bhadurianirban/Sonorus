/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.ui.dataseries;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import org.leviosa.core.driver.LeviosaClientService;
import org.hedwig.leviosa.constants.CMSConstants;
import org.hedwig.cms.dto.TermDTO;
import org.hedwig.cms.dto.TermInstanceDTO;
import org.sonorus.ui.login.CMSClientAuthCredentialValue;
import org.patronus.core.client.PatronusCoreClient;
import org.patronus.core.dto.DataSeriesDTO;
import org.patronus.constants.PatronusConstants;
import org.patronus.core.dto.FractalDTO;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author bhaduri
 */
@Named(value = "dataSeriesView")
@ViewScoped
public class DataSeriesView implements Serializable {

    private String termSlug;
    private String termName;
    private String termInstanceSlug;
    private Map<String, Object> screenTermInstance;
    private LineChartModel dataSeriesPlotModel;
    private String cumulative;

    /**
     * Creates a new instance of DataSeriesView
     */
    public DataSeriesView() {
    }

    public void getDataSeriesData() {
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
        screenTermInstance = termInstanceDTO.getTermInstance();
        int seriesId = Integer.parseInt((String) screenTermInstance.get("id"));
        int dataSeriesType = Integer.parseInt((String) screenTermInstance.get("type"));
        String dataSeriesSlug = (String)screenTermInstance.get(CMSConstants.TERM_INSTANCE_SLUG);
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO.setDataSeriesSlug(dataSeriesSlug);
        PatronusCoreClient dss = new PatronusCoreClient();
        fractalDTO = dss.getDataseries(fractalDTO);
        List<DataSeriesDTO> dataseriesList  = fractalDTO.getDataseriesDTOList();

        dataSeriesPlotModel = new LineChartModel();

        dataSeriesPlotModel.setTitle((String) screenTermInstance.get("name"));
        Axis xAxis = dataSeriesPlotModel.getAxis(AxisType.X);
        Axis yAxis = dataSeriesPlotModel.getAxis(AxisType.Y);

        LineChartSeries originalSeries = new LineChartSeries();
        originalSeries.setShowLine(false);
        //originalSeries.setShowMarker(false);
        originalSeries.setMarkerStyle("filledCircle', size:'3.0', color:'#000000");
        
        LineChartSeries cumulativeSeries = new LineChartSeries();
        cumulativeSeries.setShowLine(false);
        cumulativeSeries.setMarkerStyle("filledCircle', size:'3.0', color:'#0000FF");
        
        Double yvalue;
        Double yCumValue;
        Double xvalue;
        if (dataSeriesType == PatronusConstants.XY_SERIES) {
            Double minXValue = dataseriesList.stream().min(Comparator.comparing(d -> d.getXvalue())).get().getXvalue();
            Double maxXValue = dataseriesList.stream().max(Comparator.comparing(d -> d.getXvalue())).get().getXvalue();
            xAxis.setMin(minXValue);
            xAxis.setMax(maxXValue);
            for (DataSeriesDTO dataseries : dataseriesList) {

                xvalue = dataseries.getXvalue();
                yvalue = dataseries.getYvalue();
                originalSeries.set(xvalue, yvalue);

            }
        } else {
            if (cumulative.equals("Yes")) {
                xvalue = 0.0;
                xAxis.setMin(0);
                xAxis.setMax(dataseriesList.size());
                Double minCumYValue = dataseriesList.stream().min(Comparator.comparing(d -> d.getYcumulative())).get().getYcumulative();
                Double maxCumYValue = dataseriesList.stream().max(Comparator.comparing(d -> d.getYcumulative())).get().getYcumulative();
                Double minYValue = dataseriesList.stream().min(Comparator.comparing(d -> d.getYvalue())).get().getYvalue();
                Double maxYValue = dataseriesList.stream().max(Comparator.comparing(d -> d.getYvalue())).get().getYvalue();
                Double minYAxisValue;
                Double maxYAxisValue;
                if (minCumYValue<minYValue) {
                    minYAxisValue = minCumYValue;
                } else {
                    minYAxisValue = minYValue;
                }
                if (maxCumYValue> maxYValue) {
                    maxYAxisValue = maxCumYValue;
                } else {
                    maxYAxisValue = maxYValue;
                }
                yAxis.setMin(minYAxisValue);
                yAxis.setMax(maxYAxisValue);
                for (DataSeriesDTO dataseries : dataseriesList) {
                    xvalue = xvalue + 1;
                    yCumValue = dataseries.getYcumulative();
                    cumulativeSeries.set(xvalue, yCumValue);
                    yvalue = dataseries.getYvalue();
                    originalSeries.set(xvalue, yvalue);
                }
            } else {
                xvalue = 0.0;
                xAxis.setMin(0);
                xAxis.setMax(dataseriesList.size());
                Double minYValue = dataseriesList.stream().min(Comparator.comparing(d -> d.getYvalue())).get().getYvalue();
                Double maxYValue = dataseriesList.stream().max(Comparator.comparing(d -> d.getYvalue())).get().getYvalue();
                yAxis.setMin(minYValue);
                yAxis.setMax(maxYValue);
                for (DataSeriesDTO dataseries : dataseriesList) {
                    xvalue = xvalue + 1;
                    yvalue = dataseries.getYvalue();
                    originalSeries.set(xvalue, yvalue);
                }
            }
        }
        dataSeriesPlotModel.addSeries(originalSeries);
        dataSeriesPlotModel.addSeries(cumulativeSeries);
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

    public Map<String, Object> getScreenTermInstance() {
        return screenTermInstance;
    }

    public void setScreenTermInstance(Map<String, Object> screenTermInstance) {
        this.screenTermInstance = screenTermInstance;
    }

    public LineChartModel getDataSeriesPlotModel() {
        return dataSeriesPlotModel;
    }

    public void setDataSeriesPlotModel(LineChartModel dataSeriesPlotModel) {
        this.dataSeriesPlotModel = dataSeriesPlotModel;
    }

    public String getCumulative() {
        return cumulative;
    }

    public void setCumulative(String cumulative) {
        this.cumulative = cumulative;
    }

}
