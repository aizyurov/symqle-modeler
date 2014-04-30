package org.symqle.modeler.sql;

import java.util.List;

/**
 * @author lvovich
 */
public interface SchemaSqlModel {

    List<TableSqlModel> getTables();

    String getDatabaseName();

}
