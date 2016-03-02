package smt_visualizer.graph;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

public class DataCreator {
	
	public static DefaultCategoryDataset createDataset(List<Date> timestamp, Map<String, List<Double>> data) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		
		for (String graph : data.keySet()) {
			List<Double> vals = data.get(graph);
			
			for (int i = 0; i < vals.size() && i < timestamp.size(); i++) {
				dataset.addValue(vals.get(i), graph, timestamp.get(i));
			}
		}
		
		return dataset;
	}
	
}
