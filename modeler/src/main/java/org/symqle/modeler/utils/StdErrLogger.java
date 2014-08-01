package org.symqle.modeler.utils;

/**
 * @author lvovich
 */
public abstract class StdErrLogger extends SimpleLogger {
    protected void printLog(final String format, final Object[] args) {
        System.err.println(String.format(format,  args));
    }
}
