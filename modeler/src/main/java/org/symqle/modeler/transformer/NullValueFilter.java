package org.symqle.modeler.transformer;

/**
 * @author lvovich
 */
public class NullValueFilter extends AbstractPropertyFilter {

    @Override
    protected boolean matches(final String value) {
        return value == null;
    }
}
