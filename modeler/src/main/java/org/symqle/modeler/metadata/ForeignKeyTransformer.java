package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.DatabaseObjectModel;
import org.symqle.modeler.sql.ForeignKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.utils.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
public class ForeignKeyTransformer extends AbstractTransformer {

    private String singleColumnKey = "FKCOLUMN_NAME";
    private String multiColumnKey = "PKTABLE_NAME";

    private final List<String> FK_SUFFIXES = Arrays.asList("ID", "FK");

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        final MetadataModel model = new MetadataModel();

        copyTables(source, model);
        copyColumns(source, model);
        copyPrimaryKeys(source, model);

        for (TableSqlModel table: source.getTables()) {
            final Set<String> usedNames = new HashSet<>();

            for (ColumnSqlModel column: table.getColumns()) {
                usedNames.add(column.getProperties().get("JAVA_NAME"));
            }

            for (ForeignKeySqlModel fk: table.getForeignKeys()) {
                final List<DatabaseObjectModel> fkColumns = fk.getColumnProperties();
                final String key = fkColumns.size() > 1 ? multiColumnKey : singleColumnKey;
                final String sqlName = fk.getProperties().get(key);
                final Map<String, String> properties = new HashMap<>();
                final String camelCaseName = StringUtils.camelize(sqlName);
                String suggestedName = fkColumns.size() > 1 ? camelCaseName : camelCaseName + "Ref";
                for (String suffix : FK_SUFFIXES) {
                    if (camelCaseName.toUpperCase().endsWith(suffix) && camelCaseName.length() > suffix.length()) {
                        suggestedName = camelCaseName.substring(0, camelCaseName.length() - suffix.length());
                        break;
                    }
                }
                for (int i=0; !properties.containsKey("JAVA_NAME"); i++) {
                    final String candidate = i==0 ? suggestedName : suggestedName + i;
                    if (!usedNames.contains(candidate)) {
                        properties.put("JAVA_NAME", candidate);
                        properties.put("GETTER_NAME", "get" + StringUtils.firstToUpper(candidate));
                        usedNames.add(candidate);
                    }
                }
                boolean notNullable = true;
                for (DatabaseObjectModel fkColumn: fk.getColumnProperties()) {
                    notNullable &= "0".equals(fkColumn.getProperties().get("NULLABLE"));
                }
                properties.put("NOT_NULLABLE", String.valueOf(notNullable));
                model.addForeignKey(fkColumns, properties);
            }
        }

        return model;
    }

}
