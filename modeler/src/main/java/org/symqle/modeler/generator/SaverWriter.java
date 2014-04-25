package org.symqle.modeler.generator;

import org.symqle.modeler.sql.TableSqlModel;

/**
 * @author lvovich
 */
public class SaverWriter extends ConditionalClassWriter {

    @Override
    protected boolean mustGenerate(final TableSqlModel table) {
        return table.getPrimaryKey() != null && table.getPrimaryKey().getColumns().size() == 1;
    }
}
