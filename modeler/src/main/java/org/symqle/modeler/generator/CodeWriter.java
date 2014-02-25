package org.symqle.modeler.generator;

import org.symqle.modeler.sql.SchemaSqlModel;

import java.io.IOException;

/**
 * @author lvovich
 */
public interface CodeWriter {

    void writeCode(SchemaSqlModel model) throws IOException;
}
