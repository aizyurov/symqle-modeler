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
    public boolean accept(final DatabaseObjectModel subject) {
        final String value = subject.getProperties().get(property);
        if (value == null) {
            return false;
        }
        return acceptable(value);
    }

    protected abstract boolean acceptable(String value);

    protected final String getProperty() {
        return property;
    }
}
