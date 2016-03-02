package smt_visualizer.graph;

import java.awt.Dimension;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * 
 * @author tzwickl
 *
 */
public final class Plotter extends JFrame {
	
	private static final long serialVersionUID = 7858985943978175470L;
	
	private String xAxisTitle;
	private String yAxisTitle;
	private int xDim;
	private int yDim;

	public Plotter(String title, String xAxisTitle, String yAxisTitle, int xDim, int yDim) {
		super(title);
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		this.xDim = xDim;
		this.yDim = yDim;
	}

	public void plotData(DefaultCategoryDataset dataset) {
		JFreeChart lineChart = ChartFactory.createLineChart(super.getTitle(), this.xAxisTitle, this.yAxisTitle,
				dataset, PlotOrientation.VERTICAL, true, true, true);

		ChartPanel chartPanel = new ChartPanel(lineChart);
		chartPanel.setPreferredSize(new Dimension(xDim, yDim));
		setContentPane(chartPanel);
	}
}
