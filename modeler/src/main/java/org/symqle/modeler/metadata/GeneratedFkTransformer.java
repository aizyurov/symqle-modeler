package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnPair;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lvovich
 */
public class GeneratedFkTransformer extends AbstractTransformer {

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        final MetadataModel model = new MetadataModel(source.getDatabaseName());

        copyTables(source, model);

        for (TableSqlModel table: source.getTables()) {
            final List<ForeignKeySqlModel> foreignKeys = table.getForeignKeys();
            final Map<ColumnSqlModel, String> generatedClassNames = new HashMap<>();

            for (final ForeignKeySqlModel foreignKey : foreignKeys) {
                for (final ColumnPair columnPair : foreignKey.getMapping()) {
                    final String generatedClassName = columnPair.getSecond().getProperties().get("GENERATED_KEY");
                    if (generatedClassName == null) {
                        continue;
                    }
                    final ColumnSqlModel first = columnPair.getFirst();
                    if (!generatedClassNames.containsKey(first)) {
                        generatedClassNames.put(first, generatedClassName);
                    } else  if (!generatedClassName.equals(generatedClassNames.get(first))) {
                            // conflict! cannot assign class name to this column.
                            // type of this column will be natural and joins may not compile
                            generatedClassNames.put(first, null);
                    } // else we already have correct name in the map; do nothing
                }
            }
            for (ColumnSqlModel column: table.getColumns()) {
                final Map<String, String> properties = new HashMap<>(column.getProperties());
                final String generatedClassName = generatedClassNames.get(column);
                if (generatedClassName != null) {
                    properties.put("GENERATED_KEY", generatedClassName);
                    properties.put("JAVA_CLASS", generatedClassName);
                    properties.put("COLUMN_MAPPER", generatedClassName + ".MAPPER");
                }
                model.addColumn(new PropertyHolder(properties));
            }
        }

        copyPrimaryKeys(source, model);
        copyForeignKeys(source, model);

        return model;
    }

}
