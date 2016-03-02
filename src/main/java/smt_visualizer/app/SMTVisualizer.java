package smt_visualizer.app;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import smt_visualizer.cli.CommandLineInterfaceService;

/**
 * 
 * @author tzwickl
 *
 */
class SMTVisualizer {

	private static final Logger logger = LoggerFactory.getLogger(SMTVisualizer.class);

	public static void main(String[] args) {
		logger.info("SMTVisualizer started with the following arguments: " + Arrays.toString(args));
		CommandLineInterfaceService cliService = new CommandLineInterfaceService();
		cliService.parseArgsAndCreateConfig(args);
		
		MainWindow mainWindow = new MainWindow();
		
		mainWindow.openWindow();
	}
}
