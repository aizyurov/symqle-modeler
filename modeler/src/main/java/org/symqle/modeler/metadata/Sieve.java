package org.symqle.modeler.metadata;

import org.springframework.beans.factory.InitializingBean;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.PrimaryKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.transformer.Transformer;
import org.symqle.modeler.utils.SimpleLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class Sieve implements Transformer, InitializingBean {

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

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Filter tableFilter : tableFilters) {
            SimpleLogger.debug("Table filter - %s", tableFilter);
        }
        for (Filter columnFilter : columnFilters) {
            SimpleLogger.debug("Column filter - %s", columnFilter);
        }
        for (Filter fkFilter : foreignKeyFilters) {
            SimpleLogger.debug("Foreign key filter - %s", fkFilter);
        }
    }

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        final MetadataModel model = new MetadataModel();
        final List<TableSqlModel> acceptedTables = new ArrayList<>();
        for (TableSqlModel table : source.getTables()) {
            if (accept(table, tableFilters)) {
                acceptedTables.add(table);
                model.addTable(table);
                SimpleLogger.debug("%s added to model", table);
            } else {
                SimpleLogger.debug("%s excluded from model", table);
            }
        }
        for (TableSqlModel table : acceptedTables) {
            for (ColumnSqlModel column: table.getColumns()) {
                if (accept(column, columnFilters)) {
                    model.addColumn(column);
                    SimpleLogger.debug("Column %s added to model", column);
                } else {
                    SimpleLogger.debug("Column %s excluded from model", column);
                }
            }
        }

        for (TableSqlModel table : acceptedTables) {
            for (ForeignKeySqlModel foreignKey: table.getForeignKeys()) {
                if (accept(foreignKey, foreignKeyFilters)) {
                    model.addForeignKey(foreignKey.getColumnProperties());
                    SimpleLogger.debug("Foreign key %s added to model", foreignKey);
                } else {
                    SimpleLogger.debug("Foreign %s excluded from model", foreignKey);
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
