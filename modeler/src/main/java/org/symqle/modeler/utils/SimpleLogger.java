package org.symqle.modeler.utils;

/**
 * @author lvovich
 */
public class SimpleLogger {
    
    protected void logError(String format, Object... args) {
    }
    protected void logError(Throwable t, String format, Object... args) {
    }
    protected void logWarn(String format, Object... args) {
    }
    protected void logInfo(String format, Object... args) {
    }
    protected void logDebug(String format, Object... args) {
    }
    
    private static SimpleLogger instance = new SimpleLogger();
    
    public static void setLogger(final SimpleLogger logger) {
        instance = logger;
    }
    
    public static void error(String format, Object... args) {
        instance.logError(format, args);
    }

    public static void error(Throwable t, String format, Object... args) {
        instance.logError(t, format, args);
    }
    public static void warn(String format, Object... args) {
        instance.logWarn(format, args);
    }
    public static void info(String format, Object... args) {
        instance.logInfo(format, args);
    }
    public static void debug(String format, Object... args) {
        instance.logDebug(format, args);
    }

}
