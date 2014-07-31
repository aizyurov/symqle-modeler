package org.symqle.modeler.transformer;

import org.symqle.modeler.sql.DatabaseObjectModel;

/**
 * @author lvovich
 */
public abstract class AbstractPropertyFilter implements Filter {
    private String property;

    public void setProperty(final String property) {
        this.property = property;
    }

    @Override
    public boolean reject(final DatabaseObjectModel subject) {
        final String value = subject.getProperties().get(property);
        return notAcceptable(value);
    }

    protected abstract boolean notAcceptable(String value);

    protected final String getProperty() {
        return property;
    }
}
