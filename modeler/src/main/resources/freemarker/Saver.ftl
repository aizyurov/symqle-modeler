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
package ${package};

<#list model.primaryKey.columns as key>
  <#if !key.properties.GENERATED_KEY?? >
import org.symqle.common.Mappers;
  </#if>
</#list>

import java.util.List;
import java.util.ArrayList;
import org.symqle.jdbc.Engine;
import org.symqle.sql.GeneratedKeys;
import org.symqle.jdbc.Option;
import org.symqle.sql.SetClauseList;
import ${packages.dto}.${model.properties.JAVA_NAME}Dto;
import ${packages.model}.${model.properties.JAVA_NAME};
<#list model.generatedKeys as key>
import ${packages.dto}.${key};
</#list>
<#list model.primaryKey.columns as key>
  <#assign primaryKey = key/>
</#list>

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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
          final List<${primaryKey.properties.JAVA_CLASS}> keys = new ArrayList<>();
          final GeneratedKeys<${primaryKey.properties.JAVA_CLASS}> generatedKeys = GeneratedKeys.collect(table.${primaryKey.properties.JAVA_NAME}(), keys);
          final int affectedRows = table.insert(setList).compileUpdate(generatedKeys, engine, options).execute();
          return affectedRows == 1 ? keys.get(0) : null;
          <#else>
          final ${primaryKey.properties.JAVA_CLASS} newId = generateId();
          final SetClauseList setListWithId = setList.also(table.${primaryKey.properties.JAVA_NAME}().set(newId));
          final int affectedRows = table.insert(setListWithId).execute(engine, options);
          return affectedRows == 1 ? newId : null;
          </#if>
      } else {
          final int affectedRows = table.update(setList).where(table.${primaryKey.properties.JAVA_NAME}().eq(id)).execute(engine, options);
          return affectedRows == 1 ? id : null;
      }
    }

    public boolean delete(final ${primaryKey.properties.JAVA_CLASS} id) throws SQLException {
        final ${model.properties.JAVA_NAME} table = new ${model.properties.JAVA_NAME}();
        return 1 == table.delete().where(table.${primaryKey.properties.JAVA_NAME}().eq(id)).execute(engine, options);
    }

    public ${model.properties.JAVA_NAME}Dto getById(final ${primaryKey.properties.JAVA_CLASS} id) throws SQLException {
        final ${model.properties.JAVA_NAME} table = new ${model.properties.JAVA_NAME}();
        List<${model.properties.JAVA_NAME}Dto> list = new ${model.properties.JAVA_NAME}Select(table).where(table.${primaryKey.properties.JAVA_NAME}().eq(id)).list(engine, options);
        return list.isEmpty() ? null : list.get(0);
    }

<#if primaryKey.properties.IS_AUTOINCREMENT != "YES">
      private ${primaryKey.properties.JAVA_CLASS}  generateId() {
          throw new IllegalStateException("Not implemented");
      }
</#if>
}
</#if>


