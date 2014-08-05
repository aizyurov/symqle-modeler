/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.dao;

import org.symqle.sql.Selector;
import org.symqle.common.RowMapper;
import org.symqle.common.Row;
import org.symqle.sample.data.ProjectDto;
import org.symqle.sample.model.Project;
import java.sql.SQLException;

/**
 * Selects {@link org.symqle.sample.data.ProjectDto} from {@link org.symqle.sample.model.Project}.
 */
public class ProjectSelect extends Selector<ProjectDto> {

  private final RowMapper<Long> idMapper;
  private final RowMapper<String> nameMapper;

  /**
   * Constructs ProjectSelect for given table.
   * @param table instance of Project to select from.
   */
  public ProjectSelect(final Project table) {
      idMapper = map(table.id());
      nameMapper = map(table.name());
  }

  protected final ProjectDto create(final Row row) throws SQLException {
      final ProjectDto dto = new ProjectDto(idMapper.extract(row));
      dto.setName(nameMapper.extract(row));
      return dto;
  }
}

