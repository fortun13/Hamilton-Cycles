package chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Fortun on 2014-04-29.
 */
public class MyChartWindow extends JFrame {

    public MyChartWindow(XYSeriesCollection[] series) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 600);

        setTitle("Chart");

        final JFreeChart chart = createChart(series);

        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500,200));
        this.getContentPane().add(chartPanel);

        this.setVisible(true);
    }

    /**
     * Creates a sample chart.
     *
     * @param series  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset[] series) {
        final JFreeChart result = ChartFactory.createXYLineChart(
                "Test Chart",
                "Number of Iteration",
                "Population",
                series[0],
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        final XYPlot plot = result.getXYPlot();
        plot.setDataset(1,series[1]);
        XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer(true,false);
        XYLineAndShapeRenderer renderer1 = new XYLineAndShapeRenderer(true,false);
        plot.setRenderer(0, renderer0);
        plot.setRenderer(1, renderer1);
        plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0, Color.red);
        plot.getRendererForDataset(plot.getDataset(1)).setSeriesPaint(0, Color.blue);
        ValueAxis axis = plot.getDomainAxis();
        axis.setAutoRange(true);
        axis = plot.getRangeAxis();
        axis.setRange(0,300);
        return result;
    }
}
