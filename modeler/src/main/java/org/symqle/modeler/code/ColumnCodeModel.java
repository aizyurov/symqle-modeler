package org.symqle.modeler.code;

/**
 * @author lvovich
 */
public interface ColumnCodeModel {

    String getJavaName();
    String getJavaType();
    TableCodeModel getTable();
    String getProperty(String propertyName);
}
