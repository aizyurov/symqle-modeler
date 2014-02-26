<#ftl strip_whitespace="true">
/* THIS IS GENERATED CODE - ALL CHANGES WILL BE LOST */

<#include "PlainKeysDefinitions.ftl"/>
<#function isPrimaryKey column table>
  ${table.properties.JAVA_NAME}.${column.properties.JAVA_NAME}
  <#if ! table.primaryKey?? ><#return false></#if>
  <#list table.primaryKey.columns as pk>
      <#if pk.properties.JAVA_NAME == column.properties.JAVA_NAME>
        <#return true>
      </#if>
  </#list>
  <#return false>
</#function>
package ${packages["${package}"]};

import org.symqle.sql.Mappers;
<#list requiredImport?keys as importKey>
  <#list model.columns as column>
      <#assign javaType>${columnTypeMapping["${column.properties.DATA_TYPE}"]}</#assign>
    <#if importKey == javaType>
import ${requiredImport["${javaType}"]};
    <#break></#if>
  </#list>
</#list>

public class ${className} {
  <#list model.columns as column>
    private <#if isPrimaryKey(column, model) >final </#if>${columnTypeMapping["${column.properties.DATA_TYPE}"]} ${column.properties.JAVA_NAME};
  </#list>

   public ${model.properties.JAVA_NAME}(<#if model.primaryKey??><#list model.primaryKey.columns as column>final ${columnTypeMapping["${column.properties.DATA_TYPE}"]} ${column.properties.JAVA_NAME}</#list></#if>) {
      <#if model.primaryKey??>
          <#list model.primaryKey.columns as column>
            this.${column.properties.JAVA_NAME} = ${column.properties.JAVA_NAME};
          </#list>
        </#if>
   }

<#list model.columns as column>
  public ${columnTypeMapping["${column.properties.DATA_TYPE}"]} get${column.properties.JAVA_NAME?cap_first}() {
    return ${column.properties.JAVA_NAME};
  }
  <#if ! isPrimaryKey(column, model)>
  public set${column.properties.JAVA_NAME?cap_first}(final ${columnTypeMapping["${column.properties.DATA_TYPE}"]} ${column.properties.JAVA_NAME}) {
    this.${column.properties.JAVA_NAME} = ${column.properties.JAVA_NAME};
  }

  </#if>
</#list>
}

