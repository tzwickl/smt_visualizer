package smt_visualizer.database;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DatabaseConnection {

	Map<String, List<Number>> readDataFromCassandra(String query);

	List<Date> readTimestamp(String query);

	Set<String> getKeyspaces();

	List<String> getTables(String keyspace);

	List<String> getColumns(String keyspace, String table);

	Set<String> getHostNames(String keyspace, String table, String column);

}
