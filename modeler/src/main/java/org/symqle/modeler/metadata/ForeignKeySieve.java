package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.transformer.Filter;
import org.symqle.modeler.utils.SimpleLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class ForeignKeySieve extends AbstractTransformer {

    private List<Filter> foreignKeyFilters = new ArrayList<>();

    public void setForeignKeyFilters(final List<Filter> foreignKeyFilters) {
        this.foreignKeyFilters = foreignKeyFilters;
    }

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        for (Filter fkFilter : foreignKeyFilters) {
            SimpleLogger.info("Foreign key filter - %s", fkFilter);
        }
        final MetadataModel model = new MetadataModel(source.getDatabaseName());
        copyTables(source, model);
        copyColumns(source, model);
        for (TableSqlModel table : source.getTables()) {
            for (ForeignKeySqlModel foreignKey: table.getForeignKeys()) {
                if (accept(foreignKey, foreignKeyFilters)) {
                    model.addForeignKey(foreignKey.getColumnProperties());
                    SimpleLogger.debug("Foreign key %s added to model", foreignKey);
                } else {
                    SimpleLogger.debug("Foreign %s excluded from model", foreignKey);
                }
            }
        }
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
