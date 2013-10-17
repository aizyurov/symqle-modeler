package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.SchemaSqlModel;

import java.sql.SQLException;

/**
 * @author lvovich
 */
public interface ModelReader {
    SchemaSqlModel readModel() throws SQLException, ReflectiveOperationException;
}
