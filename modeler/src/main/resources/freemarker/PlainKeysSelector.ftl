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

import org.symqle.sql.Selector;
import org.symqle.common.RowMapper;
import org.symqle.common.Row;
import java.sql.SQLException;
import ${packages["symqle.modeler.dto.package"]}.${model.properties.JAVA_NAME}Dto;
<#list model.externalClassFqn as requiredImport>
import ${requiredImport};
</#list>
<#list model.generatedKeys as key>
import ${packages["symqle.modeler.dto.package"]}.${key};
</#list>

public class ${className} extends Selector<${model.properties.JAVA_NAME}Dto> {

<#list model.columns as column>
  private final RowMapper<${column.properties.JAVA_CLASS}> ${column.properties.JAVA_NAME}Mapper;
</#list>

  public ${className}(final ${model.properties.JAVA_NAME} table) {
<#list model.columns as column>
      ${column.properties.JAVA_NAME}Mapper = map(table.${column.properties.JAVA_NAME}());
</#list>
  }

  public ${model.properties.JAVA_NAME}Dto create(final Row row) throws SQLException {
      final ${model.properties.JAVA_NAME}Dto dto = new ${model.properties.JAVA_NAME}Dto(<#if model.primaryKey??><#list model.primaryKey.columns as column>${column.properties.JAVA_NAME}Mapper.extract(row)<#if column_has_next>,</#if></#list></#if>);
<#list model.columns as column>
          <#if ! isPrimaryKey(column, model) >
      dto.set${column.properties.JAVA_NAME?cap_first}(${column.properties.JAVA_NAME}Mapper.extract(row));
          </#if>
</#list>
      return dto;
  }
}

