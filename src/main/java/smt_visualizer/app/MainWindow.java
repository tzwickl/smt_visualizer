package smt_visualizer.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import smt_visualizer.cassandra.CassandraQueryCreator;
import smt_visualizer.csv.ExportToCSV;
import smt_visualizer.database.DatabaseConnection;
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
	private Label lblTableName;

	private DatabaseConnection databaseConnection;
	private Combo hostname;
	private Combo hostnameCol;
	private Combo timestampCol;
	private org.eclipse.swt.widgets.List cols;
	private Shell shell;
	private Combo tableName;
	private Combo keyspace;
	private Button btnConnect;

	/**
	 * @wbp.parser.entryPoint
	 */
	public void openWindow() {
		Display display = new Display();
		this.shell = new Shell(display);
		this.shell.setText("SMT Visualizer");
		this.shell.setSize(643, 705);
		GridLayout gl_shell = new GridLayout(6, false);
		this.shell.setLayout(gl_shell);

		Label lblNewLabel = new Label(this.shell, SWT.NONE);
		lblNewLabel.setText("Start Time:");

		this.startDate = new DateTime(this.shell, SWT.BORDER | SWT.CALENDAR);

		this.startTime = new DateTime(this.shell, SWT.BORDER | SWT.TIME);

		Label lblEndTime = new Label(this.shell, SWT.NONE);
		lblEndTime.setText("End Time:");

		this.endDate = new DateTime(this.shell, SWT.BORDER | SWT.CALENDAR);

		this.endTime = new DateTime(this.shell, SWT.BORDER | SWT.TIME);

		this.grpCassandraSettings = new Group(this.shell, SWT.NONE);
		this.grpCassandraSettings.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		this.grpCassandraSettings.setLayout(new GridLayout(3, false));
		this.grpCassandraSettings.setText("Cassandra Settings");

		this.lblIpaddress = new Label(this.grpCassandraSettings, SWT.NONE);
		this.lblIpaddress.setText("IP-Address");

		this.ipAddress = new Text(this.grpCassandraSettings, SWT.BORDER);
		GridData gd_ipAddress = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_ipAddress.widthHint = 200;
		this.ipAddress.setLayoutData(gd_ipAddress);

		this.btnConnect = new Button(this.grpCassandraSettings, SWT.NONE);
		this.btnConnect.setText("Connect");
		this.btnConnect.addListener(SWT.Selection, this);

		this.lblKeyspace = new Label(this.grpCassandraSettings, SWT.NONE);
		this.lblKeyspace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		this.lblKeyspace.setText("Keyspace");

		this.keyspace = new Combo(this.grpCassandraSettings, SWT.NONE);
		this.keyspace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		this.keyspace.addListener(SWT.Selection, this);

		this.lblTableName = new Label(this.grpCassandraSettings, SWT.NONE);
		this.lblTableName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		this.lblTableName.setText("Table name");

		this.tableName = new Combo(this.grpCassandraSettings, SWT.NONE);
		this.tableName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		this.tableName.addListener(SWT.Selection, this);

		this.grpInputSettings = new Group(this.shell, SWT.NONE);
		this.grpInputSettings.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 6, 1));
		this.grpInputSettings.setText("Input Settings");
		this.grpInputSettings.setLayout(new GridLayout(2, false));

		this.lblHostnameCol = new Label(this.grpInputSettings, SWT.NONE);
		this.lblHostnameCol.setText("Hostname Col:");

		this.hostnameCol = new Combo(this.grpInputSettings, SWT.NONE);
		GridData gd_hostnameCol = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_hostnameCol.widthHint = 400;
		this.hostnameCol.setLayoutData(gd_hostnameCol);
		this.hostnameCol.addListener(SWT.Selection, this);

		this.lblHostname = new Label(this.grpInputSettings, SWT.NONE);
		this.lblHostname.setText("Hostname:");

		this.hostname = new Combo(this.grpInputSettings, SWT.NONE);
		this.hostname.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		this.lblTimestamp = new Label(this.grpInputSettings, SWT.NONE);
		this.lblTimestamp.setText("Timestamp Col:");

		this.timestampCol = new Combo(this.grpInputSettings, SWT.NONE);
		this.timestampCol.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblTables = new Label(this.grpInputSettings, SWT.NONE);
		lblTables.setText("Columns:");

		this.cols = new org.eclipse.swt.widgets.List(this.grpInputSettings,
				SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		GridData gd_cols = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_cols.heightHint = 100;
		gd_cols.widthHint = 400;
		this.cols.setLayoutData(gd_cols);

		this.grpDiagramSettings = new Group(this.shell, SWT.NONE);
		this.grpDiagramSettings.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		this.grpDiagramSettings.setText("Diagram Settings");
		this.grpDiagramSettings.setLayout(new GridLayout(2, false));

		this.lblXaxis = new Label(this.grpDiagramSettings, SWT.NONE);
		this.lblXaxis.setText("x-Axis Label:");

		this.xAxisLabel = new Text(this.grpDiagramSettings, SWT.BORDER);
		GridData gd_xAxisLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_xAxisLabel.widthHint = 200;
		this.xAxisLabel.setLayoutData(gd_xAxisLabel);

		this.lblYaxisLabel = new Label(this.grpDiagramSettings, SWT.NONE);
		this.lblYaxisLabel.setText("y-Axis Label:");

		this.yAxisLabel = new Text(this.grpDiagramSettings, SWT.BORDER);
		GridData gd_yAxisLabel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_yAxisLabel.widthHint = 200;
		this.yAxisLabel.setLayoutData(gd_yAxisLabel);
		new Label(this.shell, SWT.NONE);
		new Label(this.shell, SWT.NONE);
		new Label(this.shell, SWT.NONE);
		new Label(this.shell, SWT.NONE);
		new Label(this.shell, SWT.NONE);

		this.exportToCSV = new Button(this.shell, SWT.NONE);
		this.exportToCSV.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		this.exportToCSV.setText("Export to CSV");
		this.exportToCSV.addListener(SWT.Selection, this);

		this.showDiagram = new Button(this.shell, SWT.NONE);
		this.showDiagram.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		this.showDiagram.addListener(SWT.Selection, this);
		this.showDiagram.setText("Show Diagram");
		new Label(this.shell, SWT.NONE);
		this.shell.open();

		while (!this.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	@Override
	public void handleEvent(final Event event) {
		Widget widget = event.widget;

		if (widget == this.showDiagram) {

			final String timestampQuery = getTimestampQuery();
			final String dataQuery = getDataQuery();

			Thread createDiagramThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						final List<Date> timestamp = getTimestamp(timestampQuery);
						final Map<String, List<Number>> data = getData(dataQuery);

						MainWindow.this.shell.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								DefaultCategoryDataset dataset = DataCreator.createDataset(timestamp, data);

								Plotter plotter = new Plotter(MainWindow.this.hostname.getText(),
										MainWindow.this.xAxisLabel.getText(), MainWindow.this.yAxisLabel.getText(), 600,
										600);
								plotter.plotData(dataset);

								plotter.pack();
								RefineryUtilities.centerFrameOnScreen(plotter);
								plotter.setVisible(true);
								plotter.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
								MainWindow.this.showDiagram.setEnabled(true);
								MainWindow.this.shell.layout();
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage());
						MainWindow.this.shell.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								MainWindow.this.showDiagram.setEnabled(true);
								MainWindow.this.shell.layout();
							}
						});
					}
				}
			});

			createDiagramThread.start();
			this.showDiagram.setEnabled(false);
			this.shell.layout();
		} else if (widget == this.exportToCSV) {
			String path = askUserForDirectory(this.shell, SWT.OPEN,
					"Please select the directory to export the CSV file.", null);

			if (path == null) {
				return;
			}

			final File csvFile = new File(path + "/" + this.hostname.getText() + ".csv");

			if (csvFile.exists()) {
				csvFile.delete();
			}

			try {
				csvFile.createNewFile();
			} catch (IOException e) {
				logger.error(e.getMessage());
				return;
			}

			final String timestampQuery = getTimestampQuery();
			final String dataQuery = getDataQuery();

			Thread exportToCSVThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						final List<Date> timestamp = getTimestamp(timestampQuery);
						final Map<String, List<Number>> data = getData(dataQuery);

						MainWindow.this.shell.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								ExportToCSV exporter = new ExportToCSV();
								exporter.exportToCSV(csvFile, timestamp, data, MainWindow.this.hostnameCol.getText(),
										MainWindow.this.hostname.getText(), MainWindow.this.timestampCol.getText());
								MainWindow.this.exportToCSV.setEnabled(true);
								MainWindow.this.shell.layout();
							}
						});
					} catch (Exception e) {
						logger.error(e.getMessage());
						MainWindow.this.shell.getDisplay().asyncExec(new Runnable() {

							@Override
							public void run() {
								MainWindow.this.exportToCSV.setEnabled(true);
								MainWindow.this.shell.layout();
							}
						});
					}
				}
			});

			exportToCSVThread.start();
			this.exportToCSV.setEnabled(false);
			this.shell.layout();
		} else if (widget == this.tableName) {
			final String ipAddress = this.ipAddress.getText();
			final String keyspace = this.keyspace.getText();
			final String tableName = this.tableName.getText();

			this.databaseConnection = new Cassandra(ipAddress, keyspace);
			List<String> colNames = this.databaseConnection.getColumns(keyspace, tableName);

			this.hostnameCol.removeAll();
			for (String colName : colNames) {
				this.hostnameCol.add(colName);
			}

			this.timestampCol.removeAll();
			for (String colName : colNames) {
				this.timestampCol.add(colName);
			}

			this.cols.removeAll();
			for (String colName : colNames) {
				this.cols.add(colName);
			}

			this.shell.layout();

		} else if (widget == this.hostnameCol) {
			Set<String> hostNames = this.databaseConnection.getHostNames(this.keyspace.getText(),
					this.tableName.getText(), this.hostnameCol.getText());

			this.hostname.removeAll();
			for (String hostName : hostNames) {
				this.hostname.add(hostName);
			}

			this.shell.layout();
		} else if (widget == this.keyspace) {
			this.databaseConnection = new Cassandra(this.ipAddress.getText(), this.keyspace.getText());

			List<String> tables = this.databaseConnection.getTables(this.keyspace.getText());

			this.tableName.removeAll();
			this.hostname.removeAll();
			this.timestampCol.removeAll();
			this.cols.removeAll();
			for (String table : tables) {
				this.tableName.add(table);
			}

			this.shell.layout();
		} else if (widget == this.btnConnect) {
			this.databaseConnection = new Cassandra(this.ipAddress.getText(), null);

			Set<String> keyspaces = this.databaseConnection.getKeyspaces();

			this.keyspace.removeAll();
			this.tableName.removeAll();
			this.hostname.removeAll();
			this.timestampCol.removeAll();
			this.cols.removeAll();
			for (String keyspace : keyspaces) {
				this.keyspace.add(keyspace);
			}

			this.shell.layout();
		}
	}

	public Date getStartDate() {

		Calendar startCal = GregorianCalendar.getInstance();
		startCal.set(Calendar.YEAR, this.startDate.getYear());
		startCal.set(Calendar.MONTH, this.startDate.getMonth());
		startCal.set(Calendar.DAY_OF_MONTH, this.startDate.getDay());

		startCal.set(Calendar.HOUR_OF_DAY, this.startTime.getHours());
		startCal.set(Calendar.MINUTE, this.startTime.getMinutes());
		startCal.set(Calendar.SECOND, this.startTime.getSeconds());

		return startCal.getTime();
	}

	public Date getEndDate() {
		Calendar endCal = GregorianCalendar.getInstance();
		endCal.set(Calendar.YEAR, this.endDate.getYear());
		endCal.set(Calendar.MONTH, this.endDate.getMonth());
		endCal.set(Calendar.DAY_OF_MONTH, this.endDate.getDay());

		endCal.set(Calendar.HOUR_OF_DAY, this.endTime.getHours());
		endCal.set(Calendar.MINUTE, this.endTime.getMinutes());
		endCal.set(Calendar.SECOND, this.endTime.getSeconds());

		return endCal.getTime();
	}

	public String getTimestampQuery() {
		return CassandraQueryCreator.createQuery(getStartDate(), getEndDate(), this.hostname.getText(),
				this.hostnameCol.getText(), this.keyspace.getText(), this.tableName.getText(),
				this.timestampCol.getText(), this.timestampCol.getText());
	}

	public String getDataQuery() {
		return CassandraQueryCreator.createQuery(getStartDate(), getEndDate(), this.hostname.getText(),
				this.hostnameCol.getText(), this.keyspace.getText(), this.tableName.getText(),
				this.timestampCol.getText(), this.cols.getSelection());
	}

	public List<Date> getTimestamp(final String timestampQuery) {
		if (this.databaseConnection == null) {
			return new ArrayList<>();
		}
		return this.databaseConnection.readTimestamp(timestampQuery);
	}

	public Map<String, List<Number>> getData(final String dataQuery) {
		if (this.databaseConnection == null) {
			return new HashMap<>();
		}
		return this.databaseConnection.readDataFromCassandra(dataQuery);
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
