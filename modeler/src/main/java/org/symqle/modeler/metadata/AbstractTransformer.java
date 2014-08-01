package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.PrimaryKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Transformer;

/**
 * @author lvovich
 */
public abstract class AbstractTransformer implements Transformer {
    @Override
    public abstract SchemaSqlModel transform(SchemaSqlModel source);

    protected void copyTables(final SchemaSqlModel source, final MetadataModel target) {
        for (TableSqlModel table: source.getTables()) {
            target.addTable(table);
        }
    }

    protected void copyForeignKeys(final SchemaSqlModel source, final MetadataModel target) {
        for (TableSqlModel table: source.getTables()) {
            for (ForeignKeySqlModel fk: table.getForeignKeys()) {
                target.addForeignKey(fk.getColumnProperties());
            }
        }
    }

    protected void copyColumns(final SchemaSqlModel source, final MetadataModel model) {
        for (TableSqlModel table: source.getTables()) {
            for (ColumnSqlModel column: table.getColumns()) {
                model.addColumn(column);
            }
        }
    }

    protected void copyPrimaryKeys(final SchemaSqlModel source, final MetadataModel target) {
        for (TableSqlModel table: source.getTables()) {
            final PrimaryKeySqlModel primaryKey = table.getPrimaryKey();
            if (primaryKey != null) {
                target.addPrimaryKey(primaryKey.getColumns());
            }
        }
    }
}
