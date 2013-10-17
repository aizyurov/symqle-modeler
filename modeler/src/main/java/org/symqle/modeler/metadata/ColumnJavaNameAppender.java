package org.symqle.modeler.metadata;

import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.utils.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
public class ColumnJavaNameAppender extends AbstractTransformer {

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        final MetadataModel model = new MetadataModel();

        copyTables(source, model);

        for (TableSqlModel table: source.getTables()) {
            final Set<String> usedNames = new HashSet<>();
            for (ColumnSqlModel column: table.getColumns()) {
                final String sqlName = column.getProperties().get("COLUMN_NAME");
                final Map<String, String> properties = new HashMap<>(column.getProperties());
                final String camelCaseName = StringUtils.camelize(sqlName);
                for (int i=0; !properties.containsKey("JAVA_NAME"); i++) {
                    final String candidate = i==0 ? camelCaseName : camelCaseName + i;
                    if (!usedNames.contains(candidate)) {
                        properties.put("JAVA_NAME", candidate);
                        properties.put("GETTER_NAME", "get" + StringUtils.firstToUpper(candidate));
                        usedNames.add(candidate);
                    }
                }
                model.addColumn(new PropertyHolder(properties));
            }
        }

        copyForeignKeys(source, model);

        return model;
    }

}
