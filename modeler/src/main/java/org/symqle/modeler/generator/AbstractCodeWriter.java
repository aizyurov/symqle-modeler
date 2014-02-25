package org.symqle.modeler.generator;

import org.springframework.beans.factory.annotation.Required;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author lvovich
 */
public abstract class AbstractCodeWriter implements CodeWriter {

    private String backendPackageName;
    private String clientPackageName;
    private boolean generatedPrimaryKeys;
    private String outputDirectory;

    @Required
    public void setBackendPackageName(final String backendPackageName) {
        this.backendPackageName = backendPackageName;
    }

    @Required
    public void setClientPackageName(final String clientPackageName) {
        this.clientPackageName = clientPackageName;
    }

    @Required
    public void setGeneratedPrimaryKeys(final boolean generatedPrimaryKeys) {
        this.generatedPrimaryKeys = generatedPrimaryKeys;
    }

    @Required
    public void setOutputDirectory(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void writeCode(final SchemaSqlModel model) throws IOException {
        final String backendPackageDir = backendPackageName.replaceAll("\\.", "/");
        File backendClassesDirectory = new File(outputDirectory, backendPackageDir);
        backendClassesDirectory.mkdirs();
        for (TableSqlModel table : model.getTables()) {
            try (Writer writer = new FileWriter(new File(backendClassesDirectory, table.getProperties().get("JAVA_NAME")+".java"))) {
                generateTableClass(writer, table, backendPackageName, generatedPrimaryKeys);
            }
        }
        final String clientPackageDir = clientPackageName.replaceAll("\\.", "/");
        File clientClassesDirectory = new File(outputDirectory, clientPackageDir);
        clientClassesDirectory.mkdirs();
        for (TableSqlModel table : model.getTables()) {
            try (Writer writer = new FileWriter(new File(clientClassesDirectory, table.getProperties().get("JAVA_NAME")+"SampleDto.java"))) {
                generateSampleDtoClass(writer, table, backendPackageName, generatedPrimaryKeys);
            }
        }
        if (generatedPrimaryKeys) {
            for (TableSqlModel table : model.getTables()) {
                try (Writer writer = new FileWriter(new File(clientClassesDirectory, table.getProperties().get("JAVA_NAME")+"Id.java"))) {
                    generatePrimaryKeyClass(writer, table, backendPackageName, generatedPrimaryKeys);
                }
            }
        }
    }

    protected abstract void generateTableClass(final Writer writer, final TableSqlModel table, String packageName, boolean generatedPrimaryKeys);
    protected abstract void generateSampleDtoClass(final Writer writer, final TableSqlModel table, String packageName, boolean generatedPrimaryKeys);
    protected abstract void generatePrimaryKeyClass(final Writer writer, final TableSqlModel table, String packageName, boolean generatedPrimaryKeys);
}
