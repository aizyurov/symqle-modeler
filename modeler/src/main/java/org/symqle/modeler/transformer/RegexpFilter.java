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
    protected boolean matches(final String value) {
        return pattern.matcher(value).matches();
    }


}
