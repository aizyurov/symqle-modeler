<#ftl strip_whitespace="true">
/* THIS IS GENERATED CODE - ALL CHANGES WILL BE LOST */

<#include "PlainKeysDefinitions.ftl"/>
package ${packages["${package}"]};

import org.symqle.sql.${tableTypeMapping["${model.properties.TABLE_TYPE}"]};
import org.symqle.sql.Mappers;
import org.symqle.sql.Column;
import org.symqle.util.OnDemand;
<#list requiredImport?keys as importKey>
  <#list model.columns as column>
      <#assign javaType>${columnTypeMapping["${column.properties.DATA_TYPE}"]}</#assign>
    <#if importKey == javaType>
import ${requiredImport["${javaType}"]};
    <#break></#if>
  </#list>
</#list>

public class ${model.properties.JAVA_NAME} extends ${tableTypeMapping["${model.properties.TABLE_TYPE}"]} {

   public String getTableName() {
       return "${model.properties.TABLE_NAME}";
   }
<#list model.columns as column>

  <#assign javaType>${columnTypeMapping["${column.properties.DATA_TYPE}"]}</#assign>
  public Column<${javaType}> ${column.properties.JAVA_NAME}() {
      return defineColumn(Mappers.${mapper["${javaType}"]}, "${column.properties.COLUMN_NAME}");
  }
</#list>

<#list model.foreignKeys as foreignKey>

    private OnDemand<${foreignKey.referencedTable.properties.JAVA_NAME}> ${foreignKey.properties.JAVA_NAME} = new OnDemand<${foreignKey.referencedTable.properties.JAVA_NAME}>() {
        public ${foreignKey.referencedTable.properties.JAVA_NAME} init() {
            ${foreignKey.referencedTable.properties.JAVA_NAME} other = new ${foreignKey.referencedTable.properties.JAVA_NAME}();
            leftJoin(other, <#list foreignKey.mapping as pair><#if pair_index != 0>.and(</#if>${pair.first.properties.JAVA_NAME}().eq(other.${pair.second.properties.JAVA_NAME}())<#if pair_index != 0>)</#if></#list>);
            return other;
        }
    };
    public ${foreignKey.referencedTable.properties.JAVA_NAME} ${foreignKey.properties.JAVA_NAME}() {
        return ${foreignKey.properties.JAVA_NAME}.get();
    }
</#list>
}

