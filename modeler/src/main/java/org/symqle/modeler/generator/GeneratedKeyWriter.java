package org.symqle.modeler.generator;

import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

/**
 * @author lvovich
 */
public class GeneratedKeyWriter extends ConditionalClassWriter {

    @Override
    protected boolean mustGenerate(final TableSqlModel table) {
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
