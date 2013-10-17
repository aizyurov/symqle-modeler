package org.symqle.modeler.sql;

/**
 * @author lvovich
 */
public class ColumnPair {

    private final ColumnSqlModel first;
    private final ColumnSqlModel second;

    public ColumnPair(final ColumnSqlModel first, final ColumnSqlModel second) {
        this.first = first;
        this.second = second;
    }

    public ColumnSqlModel getFirst() {
        return first;
    }

    public ColumnSqlModel getSecond() {
        return second;
    }
}
