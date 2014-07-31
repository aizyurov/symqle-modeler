package org.symqle.modeler.transformer;

import java.util.regex.Pattern;

/**
 * @author lvovich
 */
public class RegexpFilter extends AbstractPropertyFilter {
    private Pattern pattern;


    public void setPattern(final String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    @Override
    protected boolean notAcceptable(final String value) {
        return pattern.matcher(value).matches();
    }

    @Override
    public String toString() {
        return getProperty() + " matches '" + pattern +"'";
    }
}
