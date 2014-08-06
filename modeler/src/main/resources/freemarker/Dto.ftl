<#ftl strip_whitespace="true">
<#include "Definitions.ftl"/>
/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package ${package};

<#list model.generatedKeys as key>
import ${packages.dto}.${key};
</#list>

public class ${className} {
  <#list model.columns as column>
    private <#if isPrimaryKey(column) >final </#if>${column.properties.JAVA_CLASS} ${column.properties.JAVA_NAME};
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
  <#if ! isPrimaryKey(column)>
  public void set${column.properties.JAVA_NAME?cap_first}(final ${column.properties.JAVA_CLASS} ${column.properties.JAVA_NAME}) {
    this.${column.properties.JAVA_NAME} = ${column.properties.JAVA_NAME};
  }

  </#if>
</#list>
}

