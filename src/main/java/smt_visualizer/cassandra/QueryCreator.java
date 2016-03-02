package smt_visualizer.cassandra;

import java.util.Date;

/**
 * 
 * @author tzwickl
 *
 */
public class QueryCreator {

	public static String createQuery(Date startTime, Date endTime, String hostname, String hostnameCol,
			String keyspaceName, String tableName, String timestampCol, String... tables) {
		StringBuilder cols = new StringBuilder();
		String del = "";

		for (String table : tables) {
			cols.append(del).append(table);
			del = ", ";
		}

		return "SELECT " + cols + " FROM " + keyspaceName + "." + tableName + " WHERE " + timestampCol + " >= "
				+ startTime.getTime() + " AND " + timestampCol + " <= " + endTime.getTime() + " AND " + hostnameCol
				+ " = \'" + hostname + "\' ALLOW FILTERING;";
	}

	public static String createHostNamesQuery(String column, String keyspace, String table) {
		return "SELECT " + column + " FROM " + keyspace + "." + table + ";";
	}

	public static String createColumnNamesQuery(String keyspace, String table) {
		return "SELECT column_name FROM system.schema_columns WHERE keyspace_name = \'" + keyspace
				+ "\' AND columnfamily_name = \'" + table + "\';";
	}
}