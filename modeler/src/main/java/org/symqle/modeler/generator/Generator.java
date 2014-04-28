package org.symqle.modeler.generator;

import org.springframework.beans.factory.annotation.Required;
import org.symqle.modeler.metadata.ModelReader;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.transformer.Transformer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Generates code from metadata
 * @author lvovich
 */
public class Generator {

    private ModelReader modelReader;
    private List<Transformer> transformers;
    private Map<String, String> packageNames;
    private List<ClassWriter> classWriters;

    public void generate() throws IOException, SQLException, ReflectiveOperationException {
        SchemaSqlModel model = modelReader.readModel();
        for (Transformer transformer : transformers) {
            model = transformer.transform(model);
        }
        for (ClassWriter classWriter : classWriters) {
            classWriter.writeClasses(model, packageNames);
        }
    }

    @Required
    public void setModelReader(final ModelReader modelReader) {
        this.modelReader = modelReader;
    }

    @Required
    public void setTransformers(final List<Transformer> transformers) {
        this.transformers = transformers;
    }

    @Required
    public void setPackageNames(Map<String, String> packageNames) {
        this.packageNames = packageNames;
    }

    @Required
    public void setClassWriters(List<ClassWriter> classWriters) {
        this.classWriters = classWriters;
    }
}
