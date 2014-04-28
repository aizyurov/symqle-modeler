package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.PrimaryKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.transformer.Transformer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class Sieve implements Transformer {

    private List<Filter> tableFilters = new ArrayList<>();
    private List<Filter> columnFilters = new ArrayList<>();
    private List<Filter> foreignKeyFilters = new ArrayList<>();

    public void setTableFilters(final List<Filter> tableFilters) {
        this.tableFilters = tableFilters;
    }

    public void setColumnFilters(final List<Filter> columnFilters) {
        this.columnFilters = columnFilters;
    }

    public void setForeignKeyFilters(final List<Filter> foreignKeyFilters) {
        this.foreignKeyFilters = foreignKeyFilters;
    }

    private class SchemaSqlModelBuilder {
        private Map<String, DatabaseObjectModel> tables;
        private Map<String, Map<String, DatabaseObjectModel>> columns;
    }

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        final MetadataModel model = new MetadataModel();
        final List<TableSqlModel> acceptedTables = new ArrayList<>();
        for (TableSqlModel table : source.getTables()) {
            if (accept(table, tableFilters)) {
                acceptedTables.add(table);
                model.addTable(table);
            }
        }
        for (TableSqlModel table : acceptedTables) {
            for (ColumnSqlModel column: table.getColumns()) {
                if (accept(column, columnFilters)) {
                    model.addColumn(column);
                }
            }
        }

        for (TableSqlModel table : acceptedTables) {
            for (ForeignKeySqlModel foreignKey: table.getForeignKeys()) {
                if (accept(foreignKey, foreignKeyFilters)) {
                    model.addForeignKey(foreignKey.getColumnProperties());
                }
            }
            final PrimaryKeySqlModel pk = table.getPrimaryKey();
            if (pk != null) {
                model.addPrimaryKey(pk.getColumnProperties());
            }
        }

        return model;

    }

    private boolean accept(final DatabaseObjectModel model, List<Filter> filters) {
        for (Filter filter : filters) {
            if (!filter.accept(model)) {
                return false;
            }
        }
        return true;
    }
}
