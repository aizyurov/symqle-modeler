package org.symqle.modeler.metadata;

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
public class TableSieve extends AbstractTransformer {

    private List<Filter> tableFilters = new ArrayList<>();

    public void setTableFilters(final List<Filter> tableFilters) {
        this.tableFilters = tableFilters;
    }

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        for (Filter tableFilter : tableFilters) {
            SimpleLogger.info("Applying table filter: %s", tableFilter);
        }
        final MetadataModel model = new MetadataModel(source.getDatabaseName());
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
        copyColumns(source, model);
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
