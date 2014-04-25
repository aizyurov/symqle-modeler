<#ftl strip_whitespace="true">
/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

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

import org.symqle.sql.SmartSelector;
import java.sql.SQLException;
import ${packages["symqle.modeler.dto.package"]}.${model.properties.JAVA_NAME}Dto;

public class ${className} extends SmartSelector<${model.properties.JAVA_NAME}Dto> {

  private final ${model.properties.JAVA_NAME} table;

  public ${className}(final ${model.properties.JAVA_NAME} table) {
      this.table = table;
  }

  public ${model.properties.JAVA_NAME}Dto create() throws SQLException {
      final ${model.properties.JAVA_NAME}Dto dto = new ${model.properties.JAVA_NAME}Dto(<#if model.primaryKey??><#list model.primaryKey.columns as column>get(table.${column.properties.JAVA_NAME}())<#if column_has_next>,</#if></#list></#if>);
<#list model.columns as column>
          <#if ! isPrimaryKey(column, model) >
      dto.set${column.properties.JAVA_NAME?cap_first}(get(table.${column.properties.JAVA_NAME}()));
          </#if>
</#list>
      return dto;
  }
}

