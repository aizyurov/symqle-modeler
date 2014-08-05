/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.dao;

import org.symqle.sql.Selector;
import org.symqle.common.RowMapper;
import org.symqle.common.Row;
import org.symqle.sample.data.UsersDto;
import org.symqle.sample.model.Users;
import java.sql.SQLException;

/**
 * Selects {@link org.symqle.sample.data.UsersDto} from {@link org.symqle.sample.model.Users}.
 */
public class UsersSelect extends Selector<UsersDto> {

  private final RowMapper<Long> idMapper;
  private final RowMapper<String> nameMapper;
  private final RowMapper<String> emailMapper;

  /**
   * Constructs UsersSelect for given table.
   * @param table instance of Users to select from.
   */
  public UsersSelect(final Users table) {
      idMapper = map(table.id());
      nameMapper = map(table.name());
      emailMapper = map(table.email());
  }

  protected final UsersDto create(final Row row) throws SQLException {
      final UsersDto dto = new UsersDto(idMapper.extract(row));
      dto.setName(nameMapper.extract(row));
      dto.setEmail(emailMapper.extract(row));
      return dto;
  }
}

