package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.SchemaSqlModel;

/**
 * @author lvovich
 */
public abstract class AbstractTableTransformer extends AbstractTransformer {
    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {

        final MetadataModel model = new MetadataModel(source.getDatabaseName());
        transformTables(source, model);

        copyColumns(source, model);
        copyForeignKeys(source, model);
        copyPrimaryKeys(source, model);

        return model;
    }

    protected abstract void transformTables(SchemaSqlModel source, MetadataModel target);
}
