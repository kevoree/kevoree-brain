package org.kevoree.brain.learning.stl;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.github.brandtg.stl.StlResult;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class StlPlotter {


    static void plot(final StlResult stlResult) throws IOException {
        StlPlotter.plot(stlResult, "Seasonal Decomposition");
    }

    static void plot(final StlResult stlResult, final File save) throws IOException {
        StlPlotter.plot(stlResult, "Seasonal Decomposition", Minute.class, save);
    }

    static void plot(final StlResult stlResult, final String title) throws IOException {
        StlPlotter.plot(stlResult, title, Minute.class, new File("stl-decomposition.png"));
    }

    static void plot(final StlResult stlResult, final String title, final Class<?> timePeriod, final File save) throws IOException {
        final ResultsPlot plot = new ResultsPlot(stlResult, title, timePeriod);
        ChartUtilities.saveChartAsPNG(save, plot.chart, 800, 600);
    }

    static void plotOnScreen(final StlResult stlResult, final String title) {
        final ResultsPlot plot = new ResultsPlot(stlResult, title, Minute.class);
        plot.pack();
        RefineryUtilities.centerFrameOnScreen(plot);
        plot.setVisible(true);
    }

    private static class ResultsPlot extends ApplicationFrame {

        private static final long serialVersionUID = 1L;
        private final JFreeChart chart;
        private final ChartPanel chartPanel;
        private final String title;

        private final Class<?> timePeriod;
        private final double[] series;
        private final double[] seasonal;
        private final double[] trend;
        private final double[] times;
        private final double[] remainder;

        ResultsPlot(final StlResult stlResults, final String title, final Class<?> timePeriod) {
            super(title);

            this.series = stlResults.getSeries();
            this.seasonal = stlResults.getSeasonal();
            this.trend = stlResults.getTrend();
            this.times = stlResults.getTimes();
            this.remainder = stlResults.getRemainder();

            this.timePeriod = timePeriod;
            this.title = title;

            this.chart = createChart();
            this.chart.removeLegend();

            this.chartPanel = new ChartPanel(chart, true, true, true, true, true);
            this.chartPanel.setPreferredSize(new java.awt.Dimension(1000, 500));

            setContentPane(this.chartPanel);
        }

        private JFreeChart createChart() {

            final CombinedDomainXYPlot plot = new CombinedDomainXYPlot(new DateAxis("Time"));
            final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
            final ClusteredXYBarRenderer barRenderer = new ClusteredXYBarRenderer();
            final GradientPaint black = new GradientPaint(0.0f, 0.0f, Color.black, 0.0f, 0.0f, Color.black);

            final TimeSeries seriests = new TimeSeries("Series");
            final TimeSeries seasonalts = new TimeSeries("Seasonal");
            final TimeSeries trendts = new TimeSeries("Trend");
            final TimeSeries remainderts = new TimeSeries("Remainder");

            final TimeSeries[] tsArray = new TimeSeries[]{seriests, seasonalts, trendts};
            final String[] labels = new String[]{"Series", "Seasonal", "Trend"};

            for (int i = 0; i < series.length; i++) {
                final Date d = new Date((long) times[i]);
                RegularTimePeriod rtp = RegularTimePeriod.createInstance(this.timePeriod, d, TimeZone.getDefault());
                seriests.addOrUpdate(rtp, series[i]);
                seasonalts.addOrUpdate(rtp, seasonal[i]);
                trendts.addOrUpdate(rtp, trend[i]);
                remainderts.addOrUpdate(rtp, remainder[i]);
            }

            plot.setGap(10.0);
            renderer.setSeriesPaint(0, black);
            barRenderer.setSeriesPaint(0, black);
            plot.setOrientation(PlotOrientation.VERTICAL);

            for (int i = 0; i < tsArray.length; i++) {
                final XYDataset ts = new TimeSeriesCollection(tsArray[i]);
                final XYPlot p = new XYPlot(ts, new DateAxis(labels[i]), new NumberAxis(labels[i]), renderer);
                plot.add(p);
            }

            final XYDataset rts = new TimeSeriesCollection(remainderts);
            final XYDataset sts = new TimeSeriesCollection(seriests);
            final XYDataset tts = new TimeSeriesCollection(trendts);
            final XYPlot rplot = new XYPlot(rts, new DateAxis(), new NumberAxis("Remainder"), barRenderer);
            final XYPlot seriesAndTrend = new XYPlot(sts, new DateAxis(), new NumberAxis("S & T"), renderer);

            seriesAndTrend.setDataset(1, tts);
            seriesAndTrend.setRenderer(1, renderer);

            plot.add(rplot);
            plot.add(seriesAndTrend);

            return new JFreeChart(this.title, JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        }
    }


    private static double[] convert(List<Double> list) {
        double[] array = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }
}