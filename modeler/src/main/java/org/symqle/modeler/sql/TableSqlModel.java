package org.symqle.modeler.sql;

import java.util.List;
import java.util.Set;

/**
 * @author lvovich
 */
public interface TableSqlModel extends DatabaseObjectModel {

    /**
     * Columns of the table.
     * Database ordering preserved.
     * @return
     */
    List<ColumnSqlModel> getColumns();

    /**
     * List of foreign keys.
     * Database ordering preserved.
     * @return
     */
    List<ForeignKeySqlModel> getForeignKeys();

    PrimaryKeySqlModel getPrimaryKey();

    /**
     * List of referenced classes, which are in Java SE API but not in java.lang and require import.
     * @return Fully qualified names, no duplicates
     */
    Set<String> getExternalClassFqn();

    /**
     * Short names of generated keys classes used by this table
     * @return
     */
    Set<String> getGeneratedKeys();

}
