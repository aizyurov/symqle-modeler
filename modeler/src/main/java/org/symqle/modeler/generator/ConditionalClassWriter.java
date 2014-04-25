package org.symqle.modeler.generator;

import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author lvovich
 */
public abstract class ConditionalClassWriter extends FreeMarkerClassWriter {
    protected abstract boolean mustGenerate(TableSqlModel table);

    @Override
    public void writeClass(final File packageDir, final String packageKey, final TableSqlModel model, final Map<String, String> packageNames) throws IOException {
        if (mustGenerate(model)) {
            super.writeClass(packageDir, packageKey, model, packageNames);
        }
    }
}
