package org.symqle.modeler.code;

import org.symqle.modeler.sql.TableSqlModel;

import java.util.List;

/**
 * @author lvovich
 */
public interface TableCodeModel extends TableSqlModel {

    String getJavaName();
    List<ColumnCodeModel> getCodeColumns();
    List<RelationCodeModel> getRelations();
    String getProperty(String propertyName);

}
