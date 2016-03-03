package smt_visualizer.cassandra;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author tzwickl
 *
 */
public class CassandraQueryCreator {

	private static final Logger logger = LoggerFactory.getLogger(CassandraQueryCreator.class);

	public static String createQuery(Date startTime, Date endTime, String hostname, String hostnameCol,
			String keyspaceName, String tableName, String timestampCol, String... tables) {
		StringBuilder cols = new StringBuilder();
		String del = "";

		for (String table : tables) {
			cols.append(del).append(table);
			del = ", ";
		}

		String query = "SELECT " + cols + " FROM " + keyspaceName + "." + tableName + " WHERE " + timestampCol + " >= "
				+ startTime.getTime() + " AND " + timestampCol + " <= " + endTime.getTime() + " AND " + hostnameCol
				+ " = \'" + hostname + "\' ALLOW FILTERING;";

		logger.debug("Created Query: " + query);

		return query;
	}

	public static String createHostNamesQuery(String column, String keyspace, String table) {
		return "SELECT " + column + " FROM " + keyspace + "." + table + ";";
	}

	public static String createColumnNamesQuery(String keyspace, String table) {
		return "SELECT column_name FROM system.schema_columns WHERE keyspace_name = \'" + keyspace
				+ "\' AND columnfamily_name = \'" + table + "\';";
	}

	public static String createKeyspaceQuery() {
		return "SELECT keyspace_name FROM system.schema_columnfamilies;";
	}

	public static String createTableQuery(String keyspace) {
		return "SELECT columnfamily_name FROM system.schema_columnfamilies WHERE keyspace_name = \'" + keyspace + "\';";
	}
}