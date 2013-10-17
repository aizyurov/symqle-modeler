package org.symqle.modeler.transformer;

import org.symqle.modeler.sql.DatabaseObjectModel;

/**
 * @author lvovich
 */
public abstract class AbstractPropertyFilter implements Filter {
    private String property;
    private FilterOutcome matchOutcome;

    public void setProperty(final String property) {
        this.property = property;
    }

    public void setMatchOutcome(final FilterOutcome matchOutcome) {
        this.matchOutcome = matchOutcome;
    }

    @Override
    public FilterOutcome decide(final DatabaseObjectModel subject) {
        final String value = subject.getProperties().get(property);
        if (value == null) {
            return FilterOutcome.NEUTRAL;
        }
        return matches(value) ? matchOutcome : FilterOutcome.NEUTRAL;
    }

    protected abstract boolean matches(String value);
}
