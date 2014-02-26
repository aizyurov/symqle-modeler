package org.symqle.modeler.generator;

import org.springframework.beans.factory.annotation.Required;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author lvovich
 */
public class PackageWriter {

    private String packageKey;
    private ClassWriter classWriter;

    @Required
    public void setPackageKey(final String packageKey) {
        this.packageKey = packageKey;
    }

    @Required
    public void setClassWriter(final ClassWriter classWriter) {
        this.classWriter = classWriter;
    }

    public void writePackage(final String outputDirectory, final SchemaSqlModel model, final Map<String, String> packageNames) throws IOException {
        String packageName = packageNames.get(packageKey);
        final String packageSubdirName = packageName.replaceAll("\\.", "/");
        final File packageDir = new File(outputDirectory, packageSubdirName);
        if (!packageDir.mkdirs()) {
            throw new IOException("Failed to create " +packageDir.getName());
        }
        for (final TableSqlModel table : model.getTables()) {
            classWriter.writeClass(packageDir, packageKey, table, packageNames);
        }

    }
}
