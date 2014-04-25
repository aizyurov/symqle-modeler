<#ftl strip_whitespace="true">
/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

<#include "Definitions.ftl"/>
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
<#if model.primaryKey?? && (model.primaryKey.columns?size == 1)>
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
<#list model.primaryKey.columns as key>
  <#assign primaryKey = key/>
</#list>

import java.sql.SQLException;
import java.util.Arrays;

public class ${className} {

    private final Engine engine;
    private final Option[] options;

    public ${className}(final Engine engine, final Option[] options) {
        this.engine = engine;
        this.options = Arrays.copyOf(options, options.length);
    }

    public ${primaryKey.properties.JAVA_CLASS} save(final ${model.properties.JAVA_NAME}Dto dto) throws SQLException {
        final ${model.properties.JAVA_NAME} table = new ${model.properties.JAVA_NAME}();
        <#assign continued = false />
        final SetClauseList setList = <#list model.columns as column><#if ! isPrimaryKey(column, model)><#if continued>
                                .also(</#if>table.${column.properties.JAVA_NAME}().set(dto.get${column.properties.JAVA_NAME?cap_first}())<#if continued>)</#if><#assign continued = true/></#if></#list>;
      final ${primaryKey.properties.JAVA_CLASS} id = dto.get${primaryKey.properties.JAVA_NAME?cap_first}();
      if (id == null) {
          <#if primaryKey.properties.IS_AUTOINCREMENT == "YES">
          final GeneratedKeys<${primaryKey.properties.JAVA_CLASS}> generatedKeys = GeneratedKeys.create(${primaryKey.properties.COLUMN_MAPPER});
          table.insert(setList).compileUpdate(generatedKeys, engine, options).execute();
          return generatedKeys.first();
          <#else>
          final ${primaryKey.properties.JAVA_CLASS} newId = generateId();
          final SetClauseList setListWithId = setList.also(table.${primaryKey.properties.JAVA_NAME}().set(newId));
          table.insert(setListWithId).compileUpdate(engine, options).execute();
          return newId;
          </#if>
      } else {
          final int affectedRows = table.update(setList).where(table.${primaryKey.properties.JAVA_NAME}().eq(id)).compileUpdate(engine, options).execute();
          return affectedRows == 1 ? id : null;
      }
    }
<#if primaryKey.properties.IS_AUTOINCREMENT != "YES">
      private Long generateId() {
          throw new IllegalStateException("Not implemented");
      }
</#if>
}
</#if>


