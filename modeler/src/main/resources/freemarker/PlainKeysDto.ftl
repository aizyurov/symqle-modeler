<#ftl strip_whitespace="true">
/* THIS IS GENERATED CODE - ALL CHANGES WILL BE LOST */

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

<#list model.externalClassFqn as requiredImport>
import ${requiredImport};
</#list>
<#list model.generatedKeys as key>
import ${packages["symqle.modeler.dto.package"]}.${key};
</#list>

public class ${className} {
  <#list model.columns as column>
    private <#if isPrimaryKey(column, model) >final </#if>${column.properties.JAVA_CLASS} ${column.properties.JAVA_NAME};
  </#list>

   public ${className}(<#if model.primaryKey??><#list model.primaryKey.columns as column>final ${column.properties.JAVA_CLASS} ${column.properties.JAVA_NAME}<#if column_has_next>,</#if></#list></#if>) {
      <#if model.primaryKey??>
          <#list model.primaryKey.columns as column>
            this.${column.properties.JAVA_NAME} = ${column.properties.JAVA_NAME};
          </#list>
        </#if>
   }

<#list model.columns as column>
  public ${column.properties.JAVA_CLASS} get${column.properties.JAVA_NAME?cap_first}() {
    return ${column.properties.JAVA_NAME};
  }
  <#if ! isPrimaryKey(column, model)>
  public void set${column.properties.JAVA_NAME?cap_first}(final ${column.properties.JAVA_CLASS} ${column.properties.JAVA_NAME}) {
    this.${column.properties.JAVA_NAME} = ${column.properties.JAVA_NAME};
  }

  </#if>
</#list>
}

