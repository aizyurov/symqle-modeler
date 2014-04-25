package org.symqle.modeler.generator;

import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

/**
 * @author lvovich
 */
public class GeneratedKeysCondition implements ConditionalFreeMarkerClassWriter.Condition {

    @Override
    public boolean generate(final TableSqlModel table) {
        if (table.getPrimaryKey() == null) {
            return false;
        } else {
            for (final ColumnSqlModel column: table.getPrimaryKey().getColumns()) {
                if (column.getProperties().get("GENERATED_KEY") != null) {
                    return true;
                }
            }
            return false;
        }
    }
}
