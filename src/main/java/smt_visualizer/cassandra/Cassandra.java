package smt_visualizer.cassandra;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import smt_visualizer.database.DatabaseConnection;

/**
 * 
 * @author tzwickl
 *
 */
public class Cassandra implements DatabaseConnection {

	private static final Logger logger = LoggerFactory.getLogger(Cassandra.class);

	private Cluster cluster;
	private Session session;

	public Cassandra(final String host, final String keyspace) {
		this.cluster = Cluster.builder().addContactPoint(host).build();
		if (keyspace == null) {
			this.session = this.cluster.connect();
		} else {
			this.session = this.cluster.connect(keyspace);
		}
	}

	@Override
	public Map<String, List<Number>> readDataFromCassandra(final String query) {
		ResultSet results = this.session.execute(query);

		Map<String, List<Number>> map = new HashMap<String, List<Number>>();

		for (Row row : results) {
			ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
			for (int i = 0; i < columnDefinitions.size(); i++) {
				if (!map.containsKey(columnDefinitions.getName(i))) {
					map.put(columnDefinitions.getName(i), new ArrayList<Number>());
				}
			}

			for (int i = 0; i < columnDefinitions.size(); i++) {
				Object object = row.getObject(i);
				if (object != null) {
					if (object instanceof Number) {
						List<Number> list = map.get(columnDefinitions.getName(i));

						list.add((Number) object);
					} else {
						logger.error("Invalid data format: Data needs to be of format Number");
					}
				} else {
					List<Number> list = map.get(columnDefinitions.getName(i));
					list.add(0.0);
				}
			}
		}

		return map;
	}

	@Override
	public List<Date> readTimestamp(final String query) {
		List<Date> list = new ArrayList<>();

		ResultSet results = this.session.execute(query);

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

	@Override
	public Set<String> getKeyspaces() {
		ResultSet results = this.session.execute(CassandraQueryCreator.createKeyspaceQuery());

		Set<String> keyspace = new HashSet<String>();
		for (Row row : results) {
			keyspace.add(row.getString(0));
		}

		return keyspace;
	}

	@Override
	public List<String> getTables(final String keyspace) {
		ResultSet results = this.session.execute(CassandraQueryCreator.createTableQuery(keyspace));

		List<String> tables = new ArrayList<String>();
		for (Row row : results) {
			tables.add(row.getString(0));
		}

		return tables;
	}

	@Override
	public List<String> getColumns(final String keyspace, final String table) {
		ResultSet results = this.session.execute(CassandraQueryCreator.createColumnNamesQuery(keyspace, table));

		List<String> cols = new ArrayList<String>();
		for (Row row : results) {
			cols.add(row.getString(0));
		}

		return cols;
	}

	@Override
	public Set<String> getHostNames(final String keyspace, final String table, final String column) {
		ResultSet results = this.session.execute(CassandraQueryCreator.createHostNamesQuery(column, keyspace, table));

		Set<String> hostNames = new HashSet<String>();
		for (Row row : results) {
			hostNames.add(row.getString(0));
		}

		return hostNames;
	}
}
