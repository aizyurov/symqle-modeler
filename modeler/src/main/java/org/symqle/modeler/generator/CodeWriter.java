package org.symqle.modeler.generator;

import org.springframework.beans.factory.annotation.Required;
import org.symqle.modeler.sql.SchemaSqlModel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class CodeWriter {

    private String outputDirectory;
    private List<PackageWriter> packageWriters;
    private Map<String, String> packageNames;

    @Required
    public void setPackageNames(final Map<String, String> packageNames) {
        this.packageNames = packageNames;
    }

    @Required
    public void setPackageWriters(final List<PackageWriter> packageWriters) {
        this.packageWriters = packageWriters;
    }

    @Required
    public void setOutputDirectory(final String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void writeCode(final SchemaSqlModel model) throws IOException {
        for (PackageWriter packageWriter : packageWriters) {
            packageWriter.writePackage(outputDirectory, model, packageNames);
        }
    }
}
