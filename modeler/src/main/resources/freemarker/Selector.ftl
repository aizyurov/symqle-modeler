<#ftl strip_whitespace="true">
/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

<#include "Definitions.ftl"/>
package ${package};

import org.symqle.sql.Selector;
import org.symqle.common.RowMapper;
import org.symqle.common.Row;
import ${packages.dto}.${model.properties.JAVA_NAME}Dto;
import ${packages.model}.${model.properties.JAVA_NAME};
<#list model.generatedKeys as key>
import ${packages.dto}.${key};
</#list>
import java.sql.SQLException;
<#list model.externalClassFqn as requiredImport>
import ${requiredImport};
</#list>

/**
 * Selects {@link ${packages.dto}.${model.properties.JAVA_NAME}Dto} from {@link ${packages.model}.${model.properties.JAVA_NAME}}.
 */
public class ${className} extends Selector<${model.properties.JAVA_NAME}Dto> {

<#list model.columns as column>
  private final RowMapper<${column.properties.JAVA_CLASS}> ${column.properties.JAVA_NAME}Mapper;
</#list>

  /**
   * Constructs ${className} for given table.
   * @param table instance of ${model.properties.JAVA_NAME} to select from.
   */
  public ${className}(final ${model.properties.JAVA_NAME} table) {
<#list model.columns as column>
      ${column.properties.JAVA_NAME}Mapper = map(table.${column.properties.JAVA_NAME}());
</#list>
  }

  protected final ${model.properties.JAVA_NAME}Dto create(final Row row) throws SQLException {
      final ${model.properties.JAVA_NAME}Dto dto = new ${model.properties.JAVA_NAME}Dto(<#if model.primaryKey??><#list model.primaryKey.columns as column>${column.properties.JAVA_NAME}Mapper.extract(row)<#if column_has_next>,</#if></#list></#if>);
<#list model.columns as column>
          <#if ! isPrimaryKey(column) >
      dto.set${column.properties.JAVA_NAME?cap_first}(${column.properties.JAVA_NAME}Mapper.extract(row));
          </#if>
</#list>
      return dto;
  }
}

