package org.symqle.modeler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.symqle.modeler.utils.RegularStdErrLogger;
import org.symqle.modeler.utils.SimpleLogger;
import org.symqle.modeler.utils.VerboseStdErrLogger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author lvovich
 */
public class CommandLineRunner {

    private final String[] args;

    private final Options options = new Options();

    {
        options.addOption(new Option("c", "config", true, "config file location"));
        options.addOption(new Option("v", "verbose", false, "verbose logging"));
    }


    public CommandLineRunner(String[] args) {
        this.args = args;
    }

    public int run() throws IOException, SQLException, ReflectiveOperationException {
        System.err.println("symqle-modeler v. " + getVersion());
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            SimpleLogger.error(e.getMessage());
            usage();
            return 1;
        }
        final String config = commandLine.getOptionValue("c");
        File configFile = new File(config);
        if (!configFile.exists()) {
            SimpleLogger.error("Config file not found: " + configFile);
            return 1;
        }
        final Properties localProperties = new Properties();
        final InputStream stream = new FileInputStream(configFile);
        try {
            localProperties.load(stream);
        } finally {
            stream.close();
        }

        Main.promptForPasswordIfMissing(localProperties);

        SimpleLogger.setLogger(commandLine.hasOption("v") ? new VerboseStdErrLogger() : new RegularStdErrLogger());

        return new Launcher().run(localProperties);
    }


    private void usage() {
        new HelpFormatter().printHelp("java -jar symqle-modeler.jar -c configFile [-v]", options);
    }

    private String getVersion() throws IOException {
        return getClass().getPackage().getSpecificationVersion();
    }

}
