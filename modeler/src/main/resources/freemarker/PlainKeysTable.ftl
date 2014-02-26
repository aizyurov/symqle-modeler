<#ftl strip_whitespace="true">
/* THIS IS GENERATED CODE - ALL CHANGES WILL BE LOST */

<#include "PlainKeysDefinitions.ftl"/>
package ${packages["${package}"]};

import org.symqle.sql.${tableTypeMapping["${model.properties.TABLE_TYPE}"]};
import org.symqle.sql.Mappers;
import org.symqle.sql.Column;
<#list requiredImport?keys as importKey>
  <#list model.columns as column>
      <#assign javaType>${columnTypeMapping["${column.properties.DATA_TYPE}"]}</#assign>
    <#if importKey == javaType>
import ${requiredImport["${javaType}"]};
    <#break></#if>
  </#list>
</#list>

public class ${model.properties.JAVA_NAME} extends ${tableTypeMapping["${model.properties.TABLE_TYPE}"]} {

   public ${model.properties.JAVA_NAME}() {
       super("${model.properties.TABLE_NAME}");
   }
<#list model.columns as column>

  <#assign javaType>${columnTypeMapping["${column.properties.DATA_TYPE}"]}</#assign>
  public Column<${javaType}> ${column.properties.JAVA_NAME}() {
      return defineColumn(Mappers.${mapper["${javaType}"]}, "${column.properties.COLUMN_NAME}");
  }
</#list>
}

