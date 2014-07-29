<#ftl strip_whitespace="true">
/* THIS IS GENERATED CODE - ALL CHANGES WILL BE LOST */

package ${package};

<#include "Definitions.ftl"/>
import org.symqle.sql.${tableTypeMapping["${model.properties.TABLE_TYPE}"]};
import org.symqle.common.Mappers;
import org.symqle.sql.Column;
import org.symqle.common.OnDemand;
<#list model.externalClassFqn as requiredImport>
import ${requiredImport};
</#list>
<#list model.generatedKeys as key>
import ${packages.dto}.${key};
</#list>

/**
 * ${model.properties.TABLE_TYPE} ${model.properties.TABLE_NAME}<#if model.properties.REMARKS?? >
 * ${model.properties.REMARKS}</#if>
 */
public class ${model.properties.JAVA_NAME} extends ${tableTypeMapping["${model.properties.TABLE_TYPE}"]} {

   public String getTableDefinition() {
       return "${model.properties.TABLE_NAME}";
   }

<#list model.columns as column>
    /**
     * ${column.properties.COLUMN_NAME}.
     * ${column.properties.TYPE_NAME}<#if column.properties.COLUMN_SIZE??>(${column.properties.COLUMN_SIZE}<#if column.properties.DECIMAL_DIGITS?? >, ${column.properties.DECIMAL_DIGITS}</#if>)</#if><#if column.properties.IS_NULLABLE == "NO"> NOT NULL</#if><#if column.properties.COLUMN_DEF?? > DEFAULT ${column.properties.COLUMN_DEF}</#if><#if column.properties.REMARKS?? >
     * ${column.properties.REMARKS}</#if>
     * @return ${column.properties.COLUMN_NAME}
     */
    public Column<${column.properties.JAVA_CLASS}> ${column.properties.JAVA_NAME}() {
        return defineColumn(${column.properties.COLUMN_MAPPER}, "${column.properties.COLUMN_NAME}");
    }

</#list>
<#list model.foreignKeys as foreignKey>

    private OnDemand<${foreignKey.referencedTable.properties.JAVA_NAME}> ${foreignKey.properties.JAVA_NAME} = new OnDemand<${foreignKey.referencedTable.properties.JAVA_NAME}>() {
        @Override
        public ${foreignKey.referencedTable.properties.JAVA_NAME} construct() {
            ${foreignKey.referencedTable.properties.JAVA_NAME} other = new ${foreignKey.referencedTable.properties.JAVA_NAME}();
            leftJoin(other, <#list foreignKey.mapping as pair><#if pair_index != 0>.and(</#if>${pair.first.properties.JAVA_NAME}().eq(other.${pair.second.properties.JAVA_NAME}())<#if pair_index != 0>)</#if></#list>);
            return other;
        }
    };

    /**
     * Auto-join to ${foreignKey.referencedTable.properties.JAVA_NAME} by <#list foreignKey.mapping as pair>{@link #${pair.first.properties.JAVA_NAME}()}<#if pair_has_next>, </#if></#list>
     * FOREIGN KEY ${foreignKey.properties.FK_NAME} (<#list foreignKey.mapping as pair>${pair.first.properties.COLUMN_NAME}<#if pair_has_next>, </#if></#list>) REFERENCES ${foreignKey.referencedTable.properties.TABLE_NAME} (<#list foreignKey.mapping as pair>${pair.second.properties.COLUMN_NAME}<#if pair_has_next>, </#if></#list>)
     * @return joined ${foreignKey.referencedTable.properties.JAVA_NAME}.
     */
    public ${foreignKey.referencedTable.properties.JAVA_NAME} ${foreignKey.properties.JAVA_NAME}() {
        return ${foreignKey.properties.JAVA_NAME}.get();
    }
</#list>
}

