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

import org.symqle.sql.AbstractSelector;
import org.symqle.common.RowMapper;
import org.symqle.common.Row;
import java.sql.SQLException;
import ${packages["dto"]}.${model.properties.JAVA_NAME}Dto;
<#list requiredImport?keys as importKey>
  <#list model.columns as column>
      <#assign javaType>${columnTypeMapping["${column.properties.DATA_TYPE}"]}</#assign>
    <#if importKey == javaType>
import ${requiredImport["${javaType}"]};
    <#break></#if>
  </#list>
</#list>

public class ${className} extends AbstractSelector<${model.properties.JAVA_NAME}Dto> {

<#list model.columns as column>
  <#assign javaType>${columnTypeMapping["${column.properties.DATA_TYPE}"]}</#assign>
  private final RowMapper<${javaType}> ${column.properties.JAVA_NAME}Mapper;
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

