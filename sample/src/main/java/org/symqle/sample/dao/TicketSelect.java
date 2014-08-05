/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.dao;

import org.symqle.common.Row;
import org.symqle.common.RowMapper;
import org.symqle.sample.data.ProjectDto;
import org.symqle.sample.data.TicketDto;
import org.symqle.sample.data.UsersDto;
import org.symqle.sample.model.Ticket;
import org.symqle.sql.Selector;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Selects {@link org.symqle.sample.data.TicketDto} from {@link org.symqle.sample.model.Ticket}.
 */
public class TicketSelect extends Selector<TicketDto> {

  private final RowMapper<Long> idMapper;
  private final RowMapper<String> nameMapper;
  private final RowMapper<String> descriptionMapper;
  private final RowMapper<Timestamp> creationdateMapper;
  private final RowMapper<UsersDto> authorMapper;
  private final RowMapper<ProjectDto> projectMapper;
  private final RowMapper<UsersDto> assigneeMapper;
    private final RowMapper<Long> activeMapper;

  /**
   * Constructs TicketSelect for given table.
   * @param table instance of Ticket to select from.
   */
  public TicketSelect(final Ticket table) {
      idMapper = map(table.id());
      nameMapper = map(table.name());
      descriptionMapper = map(table.description());
      creationdateMapper = map(table.creationdate());
      authorMapper = map(new UsersSelect(table.author()));
      projectMapper = map(new ProjectSelect(table.project()));
      assigneeMapper = map(new UsersSelect(table.lastRecord().assignee()));
      activeMapper = map(table.lastRecord().active());
  }

  protected final TicketDto create(final Row row) throws SQLException {
      final TicketDto dto = new TicketDto(idMapper.extract(row),
              creationdateMapper.extract(row),
              authorMapper.extract(row),
              projectMapper.extract(row),
              assigneeMapper.extract(row),
              Long.valueOf(1).equals(activeMapper.extract(row)));
      dto.setName(nameMapper.extract(row));
      dto.setDescription(descriptionMapper.extract(row));
      return dto;
  }
}

