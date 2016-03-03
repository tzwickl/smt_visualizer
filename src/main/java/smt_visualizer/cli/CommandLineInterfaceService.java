package smt_visualizer.cli;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineInterfaceService {

	private final static Logger logger = LoggerFactory.getLogger(CommandLineInterfaceService.class);

	private static final String HELP_ARG = "help";
	private Options options;

	public CommandLineInterfaceService() {
		this.options = this.buildOptions();
	}

	public void parseArgsAndCreateConfig(final String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmdLine = parser.parse(this.options, args);
			this.parseHelp(cmdLine);
			this.logConfiguration();
		} catch (ParseException e) {
			System.err.println("Parsing failed. Reason: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("org.fortiss.pmwt.smt.App [OPTION]....", this.options);
			System.exit(0);
		}
	}

	private void logConfiguration() {
		logger.info("---- CONFIGURATION ----");
		logger.info("---- CONFIGURATION ----");
	}

	private void parseHelp(final CommandLine cmdLine) {
		if (cmdLine.hasOption(HELP_ARG)) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("smt_visualizer.app [OPTION]....", this.options);
		}
	}

	private Options buildOptions() {
		Option helpOption = Option.builder("h").longOpt(HELP_ARG).required(false).desc("Print this message").build();

		Options options = new Options();
		options.addOption(helpOption);

		return options;
	}

}
