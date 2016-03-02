package smt_visualizer.cassandra;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.plot.RingPlot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

/**
 * 
 * @author tzwickl
 *
 */
public class Cassandra {

	private static final Logger logger = LoggerFactory.getLogger(Cassandra.class);

	private Cluster cluster;
	private Session session;

	public Cassandra(String host, String keyspace) {
		this.cluster = Cluster.builder().addContactPoint(host).build();
		if (keyspace == null) {
			this.session = cluster.connect();
		} else {
			this.session = cluster.connect(keyspace);
		}
	}

	public Map<String, List<Double>> readDataFromCassandra(String query) {
		ResultSet results = session.execute(query);

		Map<String, List<Double>> map = new HashMap<String, List<Double>>();

		for (Row row : results) {
			ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
			for (int i = 0; i < columnDefinitions.size(); i++) {
				if (!map.containsKey(columnDefinitions.getName(i))) {
					map.put(columnDefinitions.getName(i), new ArrayList<Double>());
				}
			}

			for (int i = 0; i < columnDefinitions.size(); i++) {
				Object object = row.getObject(i);
				if (object != null) {
					if (object instanceof Number) {
						List<Double> list = map.get(columnDefinitions.getName(i));
						if (object instanceof Long) {
							Long val = (Long) object;
							list.add(val.doubleValue());
						} else if (object instanceof Integer) {
							Integer val = (Integer) object;
							list.add(val.doubleValue());
						} else {
							list.add((Double) object);
						}
					} else {
						logger.error("Invalid data format: Data needs to be of format Number");
					}
				} else {
					List<Double> list = map.get(columnDefinitions.getName(i));
					list.add(0.0);
				}
			}
		}

		return map;
	}

	public List<Date> readTimestamp(String query) {
		List<Date> list = new ArrayList<>();

		ResultSet results = session.execute(query);

		for (Row row : results) {
			Object object = row.getObject(0);

			if (object instanceof Date) {
				list.add((Date) object);
			} else {
				logger.error("Invalid timestamp format: Timestamp needs to be of format Date");
			}
		}

		return list;
	}

	public List<String> getKeyspaces() {
		ResultSet results = session.execute(QueryCreator.createKeyspaceQuery());

		List<String> keyspace = new ArrayList<String>();
		for (Row row : results) {
			keyspace.add(row.getString(0));
		}

		return keyspace;
	}

	public List<String> getTables(String keyspace) {
		ResultSet results = session.execute(QueryCreator.createTableQuery(keyspace));

		List<String> tables = new ArrayList<String>();
		for (Row row : results) {
			tables.add(row.getString(0));
		}

		return tables;
	}

	public List<String> getColumns(String keyspace, String table) {
		ResultSet results = session.execute(QueryCreator.createColumnNamesQuery(keyspace, table));

		List<String> cols = new ArrayList<String>();
		for (Row row : results) {
			cols.add(row.getString(0));
		}

		return cols;
	}

	public Set<String> getHostNames(String keyspace, String table, String column) {
		ResultSet results = session.execute(QueryCreator.createHostNamesQuery(column, keyspace, table));

		Set<String> hostNames = new HashSet<String>();
		for (Row row : results) {
			hostNames.add(row.getString(0));
		}

		return hostNames;
	}
}
