package org.symqle.modeler.sql;

/**
 * @author lvovich
 */
public interface ColumnSqlModel extends DatabaseObjectModel {

    TableSqlModel getOwner();

    boolean isForeignKey();

}
