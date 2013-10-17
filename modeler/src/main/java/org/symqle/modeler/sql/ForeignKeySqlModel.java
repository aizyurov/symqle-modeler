package org.symqle.modeler.sql;

import java.util.List;

/**
 * @author lvovich
 */
public interface ForeignKeySqlModel extends DatabaseObjectModel {

    /**
     * Mapping foreign key column -referenced table PK column
     * all FK columns belong to the same table;
     * all PK columns belong to the same table.
     * @return
     */
    List<ColumnPair> getMapping();
    TableSqlModel getTable();
    TableSqlModel getReferencedTable();
    List<DatabaseObjectModel> getColumns();

}
