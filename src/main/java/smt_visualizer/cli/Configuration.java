package smt_visualizer.cli;

import java.io.Serializable;

public class Configuration implements Serializable {

	private static final long serialVersionUID = -4273619306317175899L;
	
	public static String CSV_PATH;
	public static String CASSANDRA_IP = "192.168.22.187";
	public static String CASSANDRA_TABLE = "measurement";
	public static String CASSANDRA_KEYSPACE = "retit";

}
