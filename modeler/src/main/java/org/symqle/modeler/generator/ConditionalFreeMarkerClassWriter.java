package org.symqle.modeler.generator;

import org.symqle.modeler.sql.TableSqlModel;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author lvovich
 */
public class ConditionalFreeMarkerClassWriter extends FreeMarkerClassWriter {

    public interface Condition {
        /**
         * Return true if class must be generatre, false otherwise
         * @param table the table to inspect
         * @return decision whether to generate code
         */
        boolean generate(TableSqlModel table);
    }

    private Condition condition;

    public void setCondition(final Condition condition) {
        this.condition = condition;
    }

    @Override
    public void writeClass(final File packageDir, final String packageKey, final TableSqlModel model, final Map<String, String> packageNames) throws IOException {
        if (condition.generate(model)) {
            super.writeClass(packageDir, packageKey, model, packageNames);
        }
    }
}
