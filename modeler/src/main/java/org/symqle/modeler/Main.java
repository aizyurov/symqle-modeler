package org.symqle.modeler;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

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


    public Main(String[] args) {
        this.args = args;
        final Options  options = new Options();
        options.addOption(new Option("c", "config", true, "config file location"));
        options.addOption(new Option("e", "extra-classpath", true, "additional class path"));
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

        final String classPathValue = commandLine.getOptionValue("p");
        if (classPathValue != null) {
            final String[] classPathElements = classPathValue.split(":");
            final URL[] classPath = new URL[classPathElements.length];
            for (int i = 0; i < classPathElements.length; i++) {
                classPath[i] = new URL("file://"+classPathElements[i]);
            }
            final URLClassLoader urlClassLoader = new URLClassLoader(classPath);
            Thread.currentThread().setContextClassLoader(urlClassLoader);
        }

        new Launcher().run(localProperties);
        return 0;
    }


    private void usage() {
        formatter.printHelp("java -jar symqle-modeler.jar -c configLocation", options);
    }

    public static void main(String[] args) throws Exception {
        final int exitCode = new Main(args).run();
        System.exit(exitCode);
    }

    private String getVersion() throws IOException {
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("META-INF/maven/org.symqle/symqle-modeler/pom.properties")) {
            if (resourceAsStream == null) {
                return null;
            } else {
                Properties properties = new Properties();
                properties.load(resourceAsStream);
                return properties.getProperty("version");
            }
        }
    }

}
