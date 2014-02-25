package org.symqle.modeler.generator;

import org.symqle.modeler.sql.TableSqlModel;

import java.io.Writer;

/**
 * @author lvovich
 */
public class FreeMarkerCodeWriter extends AbstractCodeWriter {

    @Override
    protected void generateTableClass(final Writer writer, final TableSqlModel table, final String packageName, final boolean generatedPrimaryKeys) {
        // TODO implement
        throw new RuntimeException("Not implemented");
    }

    @Override
    protected void generateSampleDtoClass(final Writer writer, final TableSqlModel table, final String packageName, final boolean generatedPrimaryKeys) {
        // TODO implement
        throw new RuntimeException("Not implemented");
    }

    @Override
    protected void generatePrimaryKeyClass(final Writer writer, final TableSqlModel table, final String packageName, final boolean generatedPrimaryKeys) {
        // TODO implement
        throw new RuntimeException("Not implemented");
    }


}
