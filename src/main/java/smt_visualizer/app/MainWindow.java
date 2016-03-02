package smt_visualizer.app;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.WindowConstants;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RefineryUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smt_visualizer.cassandra.Cassandra;
import smt_visualizer.cassandra.QueryCreator;
import smt_visualizer.csv.ExportToCSV;
import smt_visualizer.graph.DataCreator;
import smt_visualizer.graph.Plotter;

public class MainWindow implements Listener {

	private static final Logger logger = LoggerFactory.getLogger(MainWindow.class);
	private DateTime startDate;
	private DateTime startTime;
	private DateTime endDate;
	private DateTime endTime;
	private Button showDiagram;
	private Button exportToCSV;
	private Label lblTimestamp;
	private Thread queryThread;
	private Label lblXaxis;
	private Text xAxisLabel;
	private Label lblYaxisLabel;
	private Text yAxisLabel;
	private Label lblHostname;
	private Group grpDiagramSettings;
	private Group grpInputSettings;
	private Label lblHostnameCol;
	private Group grpCassandraSettings;
	private Label lblIpaddress;
	private Text ipAddress;
	private Label lblKeyspace;
	private Text keyspace;
	private Label lblTableName;
	private Text tableName;
	private Button connect;

	private Cassandra cassandraConnection;
	private Combo hostname;
	private Combo hostnameCol;
	private Combo timestampCol;
	private org.eclipse.swt.widgets.List cols;
	private Shell shell;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void openWindow() {
		Display display = new Display();
		shell = new Shell(display);
		shell.setText("SMT Visualizer");
		shell.setSize(643, 705);
		GridLayout gl_shell = new GridLayout(6, false);
		shell.setLayout(gl_shell);

		Label lblNewLabel = new Label(shell, SWT.NONE);
		lblNewLabel.setText("Start Time:");

		startDate = new DateTime(shell, SWT.BORDER | SWT.CALENDAR);

		startTime = new DateTime(shell, SWT.BORDER | SWT.TIME);

		Label lblEndTime = new Label(shell, SWT.NONE);
		lblEndTime.setText("End Time:");

		endDate = new DateTime(shell, SWT.BORDER | SWT.CALENDAR);

		endTime = new DateTime(shell, SWT.BORDER | SWT.TIME);

		grpCassandraSettings = new Group(shell, SWT.NONE);
		grpCassandraSettings.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		grpCassandraSettings.setLayout(new GridLayout(2, false));
		grpCassandraSettings.setText("Cassandra Settings");

		lblIpaddress = new Label(grpCassandraSettings, SWT.NONE);
		lblIpaddress.setText("IP-Address");

		ipAddress = new Text(grpCassandraSettings, SWT.BORDER);
		GridData gd_ipAddress = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_ipAddress.widthHint = 200;
		ipAddress.setLayoutData(gd_ipAddress);

		lblKeyspace = new Label(grpCassandraSettings, SWT.NONE);
		lblKeyspace.setText("Keyspace");

		keyspace = new Text(grpCassandraSettings, SWT.BORDER);
		GridData gd_keyspace = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_keyspace.widthHint = 200;
		keyspace.setLayoutData(gd_keyspace);

		lblTableName = new Label(grpCassandraSettings, SWT.NONE);
		lblTableName.setText("Table name");

		tableName = new Text(grpCassandraSettings, SWT.BORDER);
		tableName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		connect = new Button(shell, SWT.NONE);
		connect.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		connect.setText("Connect");
		connect.addListener(SWT.Selection, this);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		grpInputSettings = new Group(shell, SWT.NONE);
		grpInputSettings.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		grpInputSettings.setText("Input Settings");
		grpInputSettings.setLayout(new GridLayout(2, false));

		lblHostnameCol = new Label(grpInputSettings, SWT.NONE);
		lblHostnameCol.setText("Hostname Col:");

		hostnameCol = new Combo(grpInputSettings, SWT.NONE);
		GridData gd_hostnameCol = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hostnameCol.widthHint = 400;
		hostnameCol.setLayoutData(gd_hostnameCol);
		hostnameCol.addListener(SWT.Selection, this);

		lblHostname = new Label(grpInputSettings, SWT.NONE);
		lblHostname.setText("Hostname:");

		hostname = new Combo(grpInputSettings, SWT.NONE);
		hostname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		lblTimestamp = new Label(grpInputSettings, SWT.NONE);
		lblTimestamp.setText("Timestamp Col:");

		timestampCol = new Combo(grpInputSettings, SWT.NONE);
		timestampCol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblTables = new Label(grpInputSettings, SWT.NONE);
		lblTables.setText("Columns:");

		cols = new org.eclipse.swt.widgets.List(grpInputSettings, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_cols = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_cols.heightHint = 100;
		gd_cols.widthHint = 400;
		cols.setLayoutData(gd_cols);

		grpDiagramSettings = new Group(shell, SWT.NONE);
		grpDiagramSettings.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		grpDiagramSettings.setText("Diagram Settings");
		grpDiagramSettings.setLayout(new GridLayout(2, false));

		lblXaxis = new Label(grpDiagramSettings, SWT.NONE);
		lblXaxis.setText("x-Axis Label:");

		xAxisLabel = new Text(grpDiagramSettings, SWT.BORDER);
		GridData gd_xAxisLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_xAxisLabel.widthHint = 200;
		xAxisLabel.setLayoutData(gd_xAxisLabel);

		lblYaxisLabel = new Label(grpDiagramSettings, SWT.NONE);
		lblYaxisLabel.setText("y-Axis Label:");

		yAxisLabel = new Text(grpDiagramSettings, SWT.BORDER);
		GridData gd_yAxisLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_yAxisLabel.widthHint = 200;
		yAxisLabel.setLayoutData(gd_yAxisLabel);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);
		new Label(shell, SWT.NONE);

		exportToCSV = new Button(shell, SWT.NONE);
		exportToCSV.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		exportToCSV.setText("Export to CSV");
		exportToCSV.addListener(SWT.Selection, this);

		showDiagram = new Button(shell, SWT.NONE);
		showDiagram.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		showDiagram.addListener(SWT.Selection, this);
		showDiagram.setText("Show Diagram");
		new Label(shell, SWT.NONE);
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	@Override
	public void handleEvent(Event event) {
		Widget widget = event.widget;

		if (widget == this.showDiagram) {

			List<Date> timestamp = getTimestamp();
			Map<String, List<Double>> data = getData();

			DefaultCategoryDataset dataset = DataCreator.createDataset(timestamp, data);

			Plotter plotter = new Plotter(this.hostname.getText(), this.xAxisLabel.getText(), this.yAxisLabel.getText(),
					600, 600);
			plotter.plotData(dataset);

			plotter.pack();
			RefineryUtilities.centerFrameOnScreen(plotter);
			plotter.setVisible(true);
			plotter.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		} else if (widget == this.exportToCSV) {
			String path = askUserForDirectory(this.shell, SWT.OPEN,
					"Please select the directory to export the CSV file.", null);
			
			if (path == null) {
				return;
			}
			
			File csvFile = new File(path + "/" + hostname.getText() + ".csv");
			
			if (csvFile.exists()) {
				csvFile.delete();
			} 
			
			try {
				csvFile.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage());
				return;
			}
			
			ExportToCSV exporter = new ExportToCSV();
			
			List<Date> timestamp = getTimestamp();
			Map<String, List<Double>> data = getData();
			
			exporter.exportToCSV(csvFile, timestamp, data, this.hostnameCol.getText(), this.hostname.getText(), this.timestampCol.getText());
		} else if (widget == this.connect) {
			final String ipAddress = this.ipAddress.getText();
			final String keyspace = this.keyspace.getText();
			final String tableName = this.tableName.getText();

			cassandraConnection = new Cassandra(ipAddress, keyspace);
			List<String> colNames = cassandraConnection.getColumns(keyspace, tableName);

			hostnameCol.removeAll();
			for (String colName : colNames) {
				this.hostnameCol.add(colName);
			}

			timestampCol.removeAll();
			for (String colName : colNames) {
				this.timestampCol.add(colName);
			}

			cols.removeAll();
			for (String colName : colNames) {
				this.cols.add(colName);
			}

			shell.layout();

		} else if (widget == this.hostnameCol) {
			Set<String> hostNames = cassandraConnection.getHostNames(this.keyspace.getText(), this.tableName.getText(),
					this.hostnameCol.getText());

			hostname.removeAll();
			for (String hostName : hostNames) {
				this.hostname.add(hostName);
			}

			shell.layout();
		}
	}

	public List<Date> getTimestamp() {
		
		Calendar startCal = GregorianCalendar.getInstance();
		startCal.set(Calendar.YEAR, startDate.getYear());
		startCal.set(Calendar.MONTH, startDate.getMonth());
		startCal.set(Calendar.DAY_OF_MONTH, startDate.getDay());

		startCal.set(Calendar.HOUR_OF_DAY, startTime.getHours());
		startCal.set(Calendar.MINUTE, startTime.getMinutes());
		startCal.set(Calendar.SECOND, startTime.getSeconds());

		Calendar endCal = GregorianCalendar.getInstance();
		endCal.set(Calendar.YEAR, endDate.getYear());
		endCal.set(Calendar.MONTH, endDate.getMonth());
		endCal.set(Calendar.DAY_OF_MONTH, endDate.getDay());

		endCal.set(Calendar.HOUR_OF_DAY, endTime.getHours());
		endCal.set(Calendar.MINUTE, endTime.getMinutes());
		endCal.set(Calendar.SECOND, endTime.getSeconds());

		final String timestampQuery = QueryCreator.createQuery(startCal.getTime(), endCal.getTime(),
				this.hostname.getText(), this.hostnameCol.getText(), this.keyspace.getText(), this.tableName.getText(),
				this.timestampCol.getText(), this.timestampCol.getText());

		return cassandraConnection.readTimestamp(timestampQuery);
	}

	public Map<String, List<Double>> getData() {

		Calendar startCal = GregorianCalendar.getInstance();
		startCal.set(Calendar.YEAR, startDate.getYear());
		startCal.set(Calendar.MONTH, startDate.getMonth());
		startCal.set(Calendar.DAY_OF_MONTH, startDate.getDay());

		startCal.set(Calendar.HOUR_OF_DAY, startTime.getHours());
		startCal.set(Calendar.MINUTE, startTime.getMinutes());
		startCal.set(Calendar.SECOND, startTime.getSeconds());

		Calendar endCal = GregorianCalendar.getInstance();
		endCal.set(Calendar.YEAR, endDate.getYear());
		endCal.set(Calendar.MONTH, endDate.getMonth());
		endCal.set(Calendar.DAY_OF_MONTH, endDate.getDay());

		endCal.set(Calendar.HOUR_OF_DAY, endTime.getHours());
		endCal.set(Calendar.MINUTE, endTime.getMinutes());
		endCal.set(Calendar.SECOND, endTime.getSeconds());

		final String query = QueryCreator.createQuery(startCal.getTime(), endCal.getTime(), this.hostname.getText(),
				this.hostnameCol.getText(), this.keyspace.getText(), this.tableName.getText(),
				this.timestampCol.getText(), this.cols.getSelection());

		return cassandraConnection.readDataFromCassandra(query);
	}

	public static final String askUserForDirectory(final Shell shell, final int style, final String title,
			final String filterPath) {
		DirectoryDialog dd = new DirectoryDialog(shell, style);
		dd.setText(title);
		dd.setFilterPath(filterPath);
		final String path = dd.open();
		if (path != null) {
			return path;
		} else {
			return null;
		}
	}
}
