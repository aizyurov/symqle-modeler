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

import org.symqle.jdbc.Engine;
import org.symqle.jdbc.GeneratedKeys;
import org.symqle.jdbc.Option;
import org.symqle.jdbc.PreparedUpdate;
import org.symqle.sql.SetClauseList;
import ${packages["symqle.modeler.dto.package"]}.${model.properties.JAVA_NAME}Dto;
<#list model.externalClassFqn as requiredImport>
import ${requiredImport};
</#list>
<#list model.generatedKeys as key>
import ${packages["symqle.modeler.dto.package"]}.${key};
</#list>

public class ${className} {

  private final ${model.properties.JAVA_NAME} table;

  public ${className}(final ${model.properties.JAVA_NAME} table) {
      this.table = table;
  }

  public PreparedUpdate compileUpdate(final ${model.properties.JAVA_NAME}Dto dto,
                  final GeneratedKeys<?> generatedKeys,
                  final Engine engine,
                  final Option... options) {
      final ${model.properties.JAVA_NAME} table = new ${model.properties.JAVA_NAME}();
      <#assign continued = false />
      final SetClauseList setList = <#list model.columns as column><#if ! isPrimaryKey(column, model)><#if continued>
                              .also(</#if>table.${column.properties.JAVA_NAME}().set(dto.get${column.properties.JAVA_NAME?cap_first}())<#if continued>)</#if><#assign continued = true/></#if></#list>;
  <#if model.primaryKey?? && (model.primaryKey.columns?size == 1)>if (<#list model.primaryKey.columns as key>dto.get${key.properties.JAVA_NAME?cap_first}() == null</#list>) {
        return table.insert(setList).compileUpdate(generatedKeys, engine, options);
    } else {
        return table.update(setList).compileUpdate(engine, options);
    }
  <#else>
    // put your code here
    throw new RuntimeException("Not implemented");
//    if (???) {
//      return table.insert(setList).compileUpdate(generatedKeys, engine, options);
//    } else {
//      return table.update(setList).compileUpdate(engine, options);
//    }
  </#if>
  }
}

