package org.symqle.modeler.generator;

import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author lvovich
 */
public interface ClassWriter {
    public void writeClasses(final SchemaSqlModel model,  final Map<String, String> packageNames)
            throws IOException;
}
