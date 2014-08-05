package org.symqle.sample.dao;

import org.symqle.common.Row;
import org.symqle.common.RowMapper;
import org.symqle.jdbc.QueryEngine;
import org.symqle.sample.data.ProjectWithUsersDto;
import org.symqle.sample.model.Project;
import org.symqle.sample.model.UserProject;
import org.symqle.sql.Selector;

import java.sql.SQLException;

/**
 * @author lvovich
 */
public class ProjectWithUsersSelect extends Selector<ProjectWithUsersDto> {

    private final RowMapper<Long> idMapper;
    private final RowMapper<String> nameMapper;

    /**
     * Constructs ProjectSelect for given table.
     * @param table instance of Project to select from.
     */
    public ProjectWithUsersSelect(final Project table) {
        idMapper = map(table.id());
        nameMapper = map(table.name());
    }

    protected final ProjectWithUsersDto create(final Row row) throws SQLException {
        final Long projectId = idMapper.extract(row);
        final ProjectWithUsersDto dto = new ProjectWithUsersDto(projectId);
        dto.setName(nameMapper.extract(row));
        final QueryEngine engine = row.getQueryEngine();
        final UserProject userProject = new UserProject();
        dto.setUsers(new UsersSelect(userProject.user()).where(userProject.projectId().eq(projectId)).list(engine));
        return dto;
    }

}
