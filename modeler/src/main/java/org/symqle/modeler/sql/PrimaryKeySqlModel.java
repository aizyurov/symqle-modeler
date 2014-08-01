package org.symqle.modeler.sql;

import java.util.List;

/**
 * @author lvovich
 */
public interface PrimaryKeySqlModel {

    /**
     * Primary key columns in KEY_SEQ order
     * @return pk columns
     */
    List<ColumnSqlModel> getColumns();

}
