package org.symqle.modeler.generator;

import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;

/**
 * @author lvovich
 */
public abstract class AbstractCodeWriter implements CodeWriter {

    private String packageName;
    private String outputDirectory;

    @Override
    public void writeCode(final SchemaSqlModel model) {
        final String packageAsDir = packageName.replaceAll("\\.", "/");
        File generatedClassesDirectory = new File(outputDirectory, packageAsDir);
        generatedClassesDirectory.mkdirs();
        for (TableSqlModel table : model.getTables()) {
            generateClass(table, packageName);
        }
    }

    protected abstract void generateClass(final TableSqlModel table, String packageName);
}
