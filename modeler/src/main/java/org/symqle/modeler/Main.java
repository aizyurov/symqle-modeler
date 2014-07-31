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
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author lvovich
 */
public class Main {

    private final String[] args;

    private final HelpFormatter formatter = new HelpFormatter();

    private final Options options;

    private final String[] requiredProperties = {"outputDirectory", "samplesDirectory", "jdbcDriver", "jdbcUrl", "jdbcUser", "dataPackage", "dataAccessPackage", "symqleModelPackage"};
    private final String passwordKey = "jdbcPassword";


    public Main(String[] args) {
        this.args = args;
        final Options  options = new Options();
        options.addOption(new Option("c", "config", true, "config file location"));
        options.addOption(new Option("v", "verbose", false, "verbose logging"));
        this.options = options;
    }

    public int run() throws IOException, SQLException, ReflectiveOperationException {
        System.err.println("symqle-modeler v. " + getVersion());
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            usage();
            return 1;
        }
        final String config = commandLine.getOptionValue("c");
        if (config == null) {
            usage();
            return 1;
        }
        File configFile = new File(config);
        if (!configFile.exists()) {
            System.err.println("Config file not found: " + configFile);
        }
        final Properties localProperties = new Properties();
        try (InputStream stream = new FileInputStream(configFile)) {
            localProperties.load(stream);
        }

        for (String key : requiredProperties) {
            if (!localProperties.containsKey(key)) {
                System.err.println("Missing required property: " + key);
                return 2;
            }
        }
        final String password = localProperties.getProperty(passwordKey);
        if (password == null) {
            final char[] passChars = System.console().readPassword("Enter database password:");
            localProperties.setProperty(passwordKey, new String(passChars));
        }

        SimpleLogger.setLogger(commandLine.hasOption("v") ? new VerboseStdErrLogger() : new RegularStdErrLogger());

        final String classPathValue = localProperties.getProperty("classpath");
        if (classPathValue != null) {
            SimpleLogger.info("Setting classpath: %s", classPathValue);
            final String[] classPathElements = classPathValue.split(":");
            final URL[] classPath = new URL[classPathElements.length];
            for (int i = 0; i < classPathElements.length; i++) {
                classPath[i] = new URL("file://"+classPathElements[i]);
            }
            final URLClassLoader urlClassLoader = new URLClassLoader(classPath);
            Thread.currentThread().setContextClassLoader(urlClassLoader);
        } else {
            SimpleLogger.warn("No classpath property in config file; jdbc drivers may be unavailable");
        }

        new Launcher().run(localProperties);
        return 0;
    }


    private void usage() {
        formatter.printHelp("java -jar symqle-modeler.jar -c configLocation [-v]", options);
    }

    public static void main(String[] args) throws Exception {
        final int exitCode = new Main(args).run();
        System.exit(exitCode);
    }

    private String getVersion() throws IOException {
        return getClass().getPackage().getSpecificationVersion();
    }

}
