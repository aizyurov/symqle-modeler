package org.symqle.modeler.generator;

import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author lvovich
 */
public interface ClassWriter {
    void writeClass(final File packageDir, final String packageName, final TableSqlModel model, final Map<String, String> packageNames) throws IOException;
}
