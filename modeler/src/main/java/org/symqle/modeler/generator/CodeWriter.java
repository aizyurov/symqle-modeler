package org.symqle.modeler.generator;

import org.symqle.modeler.sql.SchemaSqlModel;

/**
 * @author lvovich
 */
public interface CodeWriter {

    void writeCode(SchemaSqlModel model);
}
