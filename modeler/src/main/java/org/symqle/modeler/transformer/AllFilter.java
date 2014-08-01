package org.symqle.modeler.transformer;

import org.symqle.modeler.sql.DatabaseObjectModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class AllFilter implements Filter {

    private List<Filter> filters = new ArrayList<>();

    public void setFilters(final List<Filter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean reject(final DatabaseObjectModel subject) {
        for (final Filter filter : filters) {
            if (!filter.reject(subject)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return "all " + filters;
    }
}
