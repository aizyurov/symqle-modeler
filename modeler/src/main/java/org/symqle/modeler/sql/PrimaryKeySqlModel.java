package org.symqle.modeler.sql;

import java.util.List;

/**
 * @author lvovich
 */
public interface PrimaryKeySqlModel extends DatabaseObjectModel {

    /**
     * Mapping foreign key column -referenced table PK column
     * all FK columns belong to the same table;
     * all PK columns belong to the same table.
     * @return
     */
    TableSqlModel getTable();
    List<DatabaseObjectModel> getColumnProperties();
    List<ColumnSqlModel> getColumns();

}
