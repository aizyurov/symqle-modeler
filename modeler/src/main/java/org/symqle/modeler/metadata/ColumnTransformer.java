package org.symqle.modeler.metadata;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.symqle.modeler.sql.ColumnSqlModel;
import org.symqle.modeler.sql.PrimaryKeySqlModel;
import org.symqle.modeler.sql.SchemaSqlModel;
import org.symqle.modeler.sql.TableSqlModel;
import org.symqle.modeler.utils.SimpleLogger;
import org.symqle.modeler.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author lvovich
 */
public class ColumnTransformer extends AbstractTransformer {

                // Create or retrieve a JexlEngine
    private final JexlEngine jexl = new JexlEngine();


    private boolean naturalKeys;

    private static MappingRule rule(final String expression, final String javaType, final String mapper) {
        final MappingRule rule = new MappingRule();
        rule.setExpression(expression);
        rule.setJavaType(javaType);
        rule.setMapper(mapper);
        return rule;
    }

    private List<MappingRule> customRules = new ArrayList<>();
    private List<MappingRule> defaultRules = Arrays.asList(
            rule("DATA_TYPE_NAME == 'BOOLEAN'", "Boolean", "Mappers.BOOLEAN"),
            rule("DATA_TYPE_NAME == 'TINYINT' && COLUMN_SIZE == 1", "Boolean", "Mappers.BOOLEAN"),
            rule("DATA_TYPE_NAME == 'TINYINT'", "Byte", "Mappers.BYTE"),
            rule("DATA_TYPE_NAME == 'SMALLINT'", "Short", "Mappers.SHORT"),
            rule("DATA_TYPE_NAME == 'INTEGER'", "Integer", "Mappers.INTEGER"),
            rule("DATA_TYPE_NAME == 'BIGINT'", "Long", "Mappers.LONG"),
            rule("DATA_TYPE_NAME == 'BIT' && (COLUMN_SIZE == 1 || COLUMN_SIZE == null)", "Boolean", "Mappers.BOOLEAN"),
            rule("DATA_TYPE_NAME == 'BIT'", "Long", "Mappers.LONG"),
            rule("DATA_TYPE_NAME == 'FLOAT'", "Float", "Mappers.FLOAT"),
            rule("DATA_TYPE_NAME == 'REAL'", "Float", "Mappers.FLOAT"),
            rule("DATA_TYPE_NAME == 'DOUBLE'", "Double", "Mappers.DOUBLE"),
            rule("DATA_TYPE_NAME == 'NUMERIC' && COLUMN_SIZE == 1 && DECIMAL_DIGITS == 0", "Boolean", "Mappers.BOOLEAN"),
            rule("DATA_TYPE_NAME == 'NUMERIC'", "java.math.BigDecimal", "Mappers.DECIMAL"),
            rule("DATA_TYPE_NAME == 'DECIMAL'", "java.math.BigDecimal", "Mappers.DECIMAL"),
            rule("DATA_TYPE_NAME == 'CHAR'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'NCHAR'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'VARCHAR'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'NVARCHAR'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'LONGVARCHAR'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'LONGNVARCHAR'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'CLOB'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'NCLOB'", "String", "Mappers.STRING"),
            rule("DATA_TYPE_NAME == 'BINARY'", "byte[]", "Mappers.BYTES"),
            rule("DATA_TYPE_NAME == 'VARBINARY'", "byte[]", "Mappers.BYTES"),
            rule("DATA_TYPE_NAME == 'LONGVARBINARY'", "byte[]", "Mappers.BYTES"),
            rule("DATA_TYPE_NAME == 'BLOB'", "byte[]", "Mappers.BYTES"),
            rule("DATA_TYPE_NAME == 'TIMESTAMP'", "java.sql.Timestamp", "Mappers.TIMESTAMP"),
            rule("DATA_TYPE_NAME == 'DATE'", "java.sql.Date", "Mappers.DATE"),
            rule("DATA_TYPE_NAME == 'TIME'", "java.sql.Time", "Mappers.TIME")
    );



    public void setNaturalKeys(final boolean naturalKeys) {
        this.naturalKeys = naturalKeys;
    }

    public void setCustomRules(final List<MappingRule> customRules) {
        this.customRules = customRules;
    }

    @Override
    public SchemaSqlModel transform(final SchemaSqlModel source) {
        final MetadataModel model = new MetadataModel(source.getDatabaseName());

        copyTables(source, model);



        for (TableSqlModel table: source.getTables()) {
            final Set<String> usedNames = new HashSet<>();
            for (ColumnSqlModel column: table.getColumns()) {
                final String dataType = column.getProperties().get("DATA_TYPE");
                final int dataTypeId = Integer.valueOf(dataType);
                final String dataTypeName = TypesConstants.getTypeName(dataTypeId);
                final Map<String, String> properties = new HashMap<>(column.getProperties());
                properties.put("DATA_TYPE_NAME", dataTypeName);
                final String sqlName = properties.get("COLUMN_NAME");
                final String camelCaseName = StringUtils.camelize(sqlName);
                for (int i=0; !properties.containsKey("JAVA_NAME"); i++) {
                    final String candidate = i==0 ? camelCaseName : camelCaseName + i;
                    if (!usedNames.contains(candidate)) {
                        properties.put("JAVA_NAME", candidate);
                        usedNames.add(candidate);
                    }
                }
                addMapping(column, properties);
                model.addColumn(new PropertyHolder(properties));
            }
        }

        copyPrimaryKeys(source, model);
        copyForeignKeys(source, model);

        return model;
    }

    private void addMapping(final ColumnSqlModel column, final Map<String, String> properties) {
        if (naturalKeys || !mustGenerateKey(column)) {
            final List<MappingRule> allRules = new ArrayList<>();
            allRules.addAll(customRules);
            allRules.addAll(defaultRules);
            JexlContext jc = new MapContext();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                jc.set(entry.getKey(), entry.getValue());
            }
            for (MappingRule rule : allRules) {
                final Expression expression = jexl.createExpression(rule.getExpression());
                final boolean accept = (Boolean) expression.evaluate(jc);
                if (accept) {
                    SimpleLogger.debug("%s.%s %s(%s) -> %s", properties.get("TABLE_NAME"), properties.get("COLUMN_NAME"), properties.get("DATA_TYPE_NAME"), properties.get("COLUMN_SIZE"), rule.getJavaType() );
                    properties.put("JAVA_CLASS", rule.getJavaType());
                    properties.put("COLUMN_MAPPER", rule.getMapper());
                    return;
                }
            }
            // no rule matched - give up
            properties.put("JAVA_CLASS", "Void");
            properties.put("COLUMN_MAPPER", "Mappers.VOID");
        } else {
            final String keyClassName = column.getOwner().getProperties().get("JAVA_NAME") + "Id";
            properties.put("JAVA_CLASS", keyClassName);
            properties.put("COLUMN_MAPPER", keyClassName + ".MAPPER");
            properties.put("GENERATED_KEY", keyClassName);
            properties.put("GENERATED_KEY_OWNER", "true");
        }
    }

    private boolean mustGenerateKey(final ColumnSqlModel column) {
        final PrimaryKeySqlModel primaryKey = column.getOwner().getPrimaryKey();
        return primaryKey != null && primaryKey.getColumns().contains(column) && primaryKey.getColumns().size() == 1
                && "YES".equals(column.getProperties().get("IS_AUTOINCREMENT"));
    }

}
