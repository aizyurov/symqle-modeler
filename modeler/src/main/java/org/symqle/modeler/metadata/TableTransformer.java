package org.symqle.modeler.metadata;

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
public class TableTransformer extends AbstractTableTransformer {

    @Override
    protected void transformTables(final SchemaSqlModel source, final MetadataModel target) {
        final Set<String> usedNames = new HashSet<>();
        for (TableSqlModel table: source.getTables()) {
            final Map<String,String> properties = new HashMap<>(table.getProperties());
            final String sqlName = properties.get("TABLE_NAME");
            final String camelCaseName = StringUtils.firstToUpper(StringUtils.camelize(sqlName));
            for (int i=0; !properties.containsKey("JAVA_NAME"); i++) {
                final String candidate = i==0 ? camelCaseName : camelCaseName + i;
                if (!usedNames.contains(candidate)) {
                    properties.put("JAVA_NAME", candidate);
                    usedNames.add(candidate);
                }
            }
            target.addTable(new PropertyHolder(properties));
        }
    }

}
