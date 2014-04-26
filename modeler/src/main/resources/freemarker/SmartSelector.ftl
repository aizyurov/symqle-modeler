<#ftl strip_whitespace="true">
/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

<#include "Definitions.ftl"/>
package ${package};

import org.symqle.sql.SmartSelector;
import ${packages.dto}.${model.properties.JAVA_NAME}Dto;
import ${packages.model}.${model.properties.JAVA_NAME};

import java.sql.SQLException;

/**
 * Selects {@link ${packages.dto}.${model.properties.JAVA_NAME}Dto} from {@link ${packages.model}.${model.properties.JAVA_NAME}}.
 */
public class ${className} extends SmartSelector<${model.properties.JAVA_NAME}Dto> {

  private final ${model.properties.JAVA_NAME} table;

  /**
   * Constructs ${className} for given table.
   * @param table instance of ${model.properties.JAVA_NAME} to select from.
   */
  public ${className}(final ${model.properties.JAVA_NAME} table) {
      this.table = table;
  }

  protected final ${model.properties.JAVA_NAME}Dto create() throws SQLException {
      final ${model.properties.JAVA_NAME}Dto dto = new ${model.properties.JAVA_NAME}Dto(<#if model.primaryKey??><#list model.primaryKey.columns as column>get(table.${column.properties.JAVA_NAME}())<#if column_has_next>,</#if></#list></#if>);
<#list model.columns as column>
          <#if ! isPrimaryKey(column) >
      dto.set${column.properties.JAVA_NAME?cap_first}(get(table.${column.properties.JAVA_NAME}()));
          </#if>
</#list>
      return dto;
  }
}

