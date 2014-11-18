package org.symqle.modeler.cli;

import java.util.Properties;

/**
 * @author lvovich
 */
public class Main {
    private static String passwordKey = "jdbcPassword";

    public static void main(String[] args) throws Exception {
        final int exitCode = new CommandLineRunner(args).run();
        System.exit(exitCode);
    }

    static void promptForPasswordIfMissing(final Properties localProperties) {
        final String password = localProperties.getProperty(passwordKey);
        if (password == null) {
            final char[] passChars = System.console().readPassword("Enter database password:");
            localProperties.setProperty(passwordKey, new String(passChars));
        }
    }
}
