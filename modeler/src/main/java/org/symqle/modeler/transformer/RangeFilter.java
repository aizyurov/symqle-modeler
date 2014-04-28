package org.symqle.modeler.transformer;

/**
 * @author lvovich
 */
public class RangeFilter extends AbstractPropertyFilter {

    private int lowerBound;
    private int upperBound;

    public void setLowerBound(final int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(final int upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    protected boolean acceptable(final String value) {
        int intValue = Integer.valueOf(value);
        return intValue >= lowerBound && intValue <= upperBound;
    }
}
