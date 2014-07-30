package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnPair;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

        final Map<ColumnSqlModel, Map<String, String>> propertiesByColumn = new LinkedHashMap<>();
        for (TableSqlModel table: source.getTables()) {
            for (ColumnSqlModel column : table.getColumns()) {
                propertiesByColumn.put(column, new HashMap<>(column.getProperties()));
            }
        }

        boolean propertiesAdded;
        do {
            propertiesAdded = false;
            for (TableSqlModel table: source.getTables()) {
                final List<ForeignKeySqlModel> foreignKeys = table.getForeignKeys();

                for (final ForeignKeySqlModel foreignKey : foreignKeys) {
                    final List<ColumnPair> mapping = foreignKey.getMapping();
                    if (mapping.size() == 1) {
                        final ColumnPair columnPair = mapping.get(0);
                        final ColumnSqlModel referent = columnPair.getSecond();
                        final Map<String, String> referentProperties = propertiesByColumn.get(referent);
                        final ColumnSqlModel referral = columnPair.getFirst();
                        final Map<String, String> referralProperties = propertiesByColumn.get(referral);
                        if (referentProperties.containsKey("GENERATED_KEY")
                                && !referralProperties.containsKey("GENERATED_KEY")) {
                            referralProperties.put("GENERATED_KEY", referentProperties.get("GENERATED_KEY"));
                            referralProperties.put("JAVA_CLASS", referentProperties.get("JAVA_CLASS"));
                            referralProperties.put("COLUMN_MAPPER", referentProperties.get("COLUMN_MAPPER"));
                            propertiesAdded = true;
                        }
                    }
                }
            }
        } while (propertiesAdded);

        for (final Map<String, String> properties : propertiesByColumn.values()) {
            model.addColumn(new PropertyHolder(properties));
        }

        copyPrimaryKeys(source, model);
        copyForeignKeys(source, model);

        return model;
    }

}
