package smt_visualizer.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportToCSV {

	private static final Logger logger = LoggerFactory.getLogger(ExportToCSV.class);
	private String seperator = ";";

	public void exportToCSV(final File targetFile, final List<Date> timestamp, final Map<String, List<Number>> data,
			final String hostnameCol, final String hostname, final String timestampCol) {
		try {
			FileWriter writer = new FileWriter(targetFile);

			StringBuilder stringBuilder = new StringBuilder();
			String del = "";

			stringBuilder.append(hostnameCol).append(this.seperator).append(timestampCol).append(this.seperator);

			for (String header : data.keySet()) {
				stringBuilder.append(del).append(header);
				del = this.seperator;
			}

			writer.write(stringBuilder.toString());
			writer.append(System.lineSeparator());

			for (int row = 0; row < timestamp.size(); row++) {
				stringBuilder = new StringBuilder();
				del = "";
				stringBuilder.append(hostname).append(this.seperator).append(timestamp.get(row)).append(this.seperator);
				for (List<Number> vals : data.values()) {
					stringBuilder.append(del).append(vals.get(row));
					del = this.seperator;
				}

				writer.write(stringBuilder.toString());
				writer.append(System.lineSeparator());
			}

			writer.close();

		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

}
