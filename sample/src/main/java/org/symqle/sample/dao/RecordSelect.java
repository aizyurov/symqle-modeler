/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.dao;

import org.symqle.sample.data.UsersDto;
import org.symqle.sql.Selector;
import org.symqle.common.RowMapper;
import org.symqle.common.Row;
import org.symqle.sample.data.RecordDto;
import org.symqle.sample.model.Record;
import java.sql.SQLException;

/**
 * Selects {@link org.symqle.sample.data.RecordDto} from {@link org.symqle.sample.model.Record}.
 */
public class RecordSelect extends Selector<RecordDto> {

  private final RowMapper<String> commentMapper;
  private final RowMapper<UsersDto> editorMapper;
  private final RowMapper<UsersDto> assigneeMapper;
  private final RowMapper<Boolean> activeMapper;

  /**
   * Constructs RecordSelect for given table.
   * @param table instance of Record to select from.
   */
  public RecordSelect(final Record table) {
      commentMapper = map(table.comment());
      editorMapper = map(new UsersSelect(table.editor()));
      assigneeMapper = map(new UsersSelect(table.assignee()));
      activeMapper = map(table.active());
  }

  protected final RecordDto create(final Row row) throws SQLException {
      return new RecordDto(
              commentMapper.extract(row),
              editorMapper.extract(row),
              assigneeMapper.extract(row),
              activeMapper.extract(row));
  }
}

