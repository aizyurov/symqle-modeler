package org.symqle.modeler.utils;

/**
* @author lvovich
*/
public class RegularStdErrLogger extends StdErrLogger {
    @Override
    protected void logError(final String format, final Object... args) {
        printLog(format, args);
    }

    @Override
    protected void logWarn(final String format, final Object... args) {
        printLog(format, args);
    }

    @Override
    protected void logInfo(final String format, final Object... args) {
        printLog(format, args);
    }

    @Override
    protected void logDebug(final String format, final Object... args) {
        // do nothing
    }
}
