package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.utils.SimpleLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class ColumnSieve extends AbstractTransformer {

    private List<Filter> columnFilters = new ArrayList<>();

    public void setColumnFilters(final List<Filter> columnFilters) {
        this.columnFilters = columnFilters;
    }

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        for (Filter columnFilter : columnFilters) {
            SimpleLogger.info("Applying column filter: %s", columnFilter);
        }
        final MetadataModel model = new MetadataModel(source.getDatabaseName());

        copyTables(source, model);
        for (TableSqlModel table : source.getTables()) {
            for (ColumnSqlModel column: table.getColumns()) {
                if (accept(column, columnFilters)) {
                    model.addColumn(column);
                    SimpleLogger.debug("Column %s added to model", column);
                } else {
                    SimpleLogger.debug("Column %s excluded from model", column);
                }
            }
        }
        copyForeignKeys(source, model);
        copyPrimaryKeys(source, model);

        return model;

    }

    private boolean accept(final DatabaseObjectModel model, List<Filter> filters) {
        for (Filter filter : filters) {
            if (filter.reject(model)) {
                return false;
            }
        }
        return true;
    }
}
