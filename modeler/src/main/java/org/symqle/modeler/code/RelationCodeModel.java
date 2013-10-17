package org.symqle.modeler.code;

import java.util.Map;

/**
 * @author lvovich
 */
public interface RelationCodeModel {

    String getJavaName();
    Map<ColumnCodeModel, ColumnCodeModel> getMapping();
    TableCodeModel getReferent();
    TableCodeModel getTable();
    String getProperty(String propertyName);

}
