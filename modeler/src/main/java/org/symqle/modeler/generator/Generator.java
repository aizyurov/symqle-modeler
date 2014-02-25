package org.symqle.modeler.generator;

import org.symqle.modeler.metadata.ModelReader;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.transformer.Transformer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Generates code from metadata
 * @author lvovich
 */
public class Generator {

    private ModelReader modelReader;
    private List<Transformer> transformers;
    private CodeWriter codeWriter;

    public void generate() throws IOException, SQLException, ReflectiveOperationException {
        SchemaSqlModel model = modelReader.readModel();
        for (Transformer transformer : transformers) {
            model = transformer.transform(model);
        }
        codeWriter.writeCode(model);
    }

    public void setModelReader(final ModelReader modelReader) {
        this.modelReader = modelReader;
    }

    public void setTransformers(final List<Transformer> transformers) {
        this.transformers = transformers;
    }

    public void setCodeWriter(final CodeWriter codeWriter) {
        this.codeWriter = codeWriter;
    }
}
