package org.symqle.sample.dao;

import org.symqle.jdbc.Engine;
import org.symqle.jdbc.Option;
import org.symqle.sample.data.NewTicketDto;
import org.symqle.sample.data.ProjectDto;
import org.symqle.sample.data.ProjectWithUsersDto;
import org.symqle.sample.data.RecordDto;
import org.symqle.sample.data.TicketDto;
import org.symqle.sample.data.UsersDto;
import org.symqle.sample.model.Project;
import org.symqle.sample.model.Record;
import org.symqle.sample.model.Ticket;
import org.symqle.sample.model.UserProject;
import org.symqle.sample.model.Users;
import org.symqle.sql.GeneratedKeys;
import org.symqle.sql.SetClauseList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class SymqleDao {

    private final Engine engine;
    private final Option[] options;

    public SymqleDao(final Engine engine, final Option... options) {
        this.engine = engine;
        this.options = options;
    }

    public Long saveProject(final ProjectWithUsersDto dto) throws SQLException {
        final Project table = new Project();
        final SetClauseList setList = table.name().set(dto.getName());
      final Long id = dto.getId();
      if (id == null) {
          final List<Long> keys = new ArrayList<>();
          final GeneratedKeys<Long> generatedKeys = GeneratedKeys.collect(table.id(), keys);
          final int affectedRows = table.insert(setList).compileUpdate(generatedKeys, engine, options).execute();
          if (affectedRows == 0) {
              return null;
          }
          final Long projectId = keys.get(0);
          saveProjectUsers(dto, projectId);
          return projectId;
      } else {
          final int affectedRows = table.update(setList).where(table.id().eq(id)).execute(engine, options);
          if (affectedRows == 0) {
              return null;
          }
          final UserProject userProject = new UserProject();
          userProject.delete().where(userProject.projectId().eq(id)).execute(engine);
          saveProjectUsers(dto, id);
          return id;
      }
    }

    private void saveProjectUsers(final ProjectWithUsersDto dto, final Long projectId) throws SQLException {
        final UserProject userProject = new UserProject();
        for (UsersDto user : dto.getUsers()) {
            if (user.getId() == null) {
                throw new IllegalArgumentException("You can add only existing users to a project");
            }
            userProject.insert(userProject.projectId().set(projectId).also(userProject.userId().set(user.getId()))).execute(engine);
        }
    }

    public List<ProjectDto> listProjects() throws SQLException {
        final Project table = new Project();
        return new ProjectSelect(table).list(engine, options);
    }

    public Long saveUser(final UsersDto dto) throws SQLException {
        final Users table = new Users();
        final SetClauseList setList = table.name().set(dto.getName())
                                .also(table.email().set(dto.getEmail()));
      final Long id = dto.getId();
      if (id == null) {
          final List<Long> keys = new ArrayList<>();
          final GeneratedKeys<Long> generatedKeys = GeneratedKeys.collect(table.id(), keys);
          final int affectedRows = table.insert(setList).compileUpdate(generatedKeys, engine, options).execute();
          return affectedRows == 1 ? keys.get(0) : null;
      } else {
          final int affectedRows = table.update(setList).where(table.id().eq(id)).execute(engine, options);
          return affectedRows == 1 ? id : null;
      }

    }

    public Long createTicket(final NewTicketDto dto, final Long authorId) throws SQLException {
        final Ticket table = new Ticket();
        final SetClauseList setList = table.name().set(dto.getName())
                                .also(table.description().set(dto.getDescription()))
                                .also(table.creationdate().set(new Timestamp(System.currentTimeMillis())))
                                .also(table.authorId().set(authorId))
                                .also(table.projectId().set(dto.getProjectId()));
        final List<Long> keys = new ArrayList<>();
        final GeneratedKeys<Long> generatedKeys = GeneratedKeys.collect(table.id(), keys);
        final int affectedRows = table.insert(setList).compileUpdate(generatedKeys, engine, options).execute();
        return affectedRows == 1 ? keys.get(0) : null;
    }

    public void updateTicket(final TicketDto dto, final Long editorId) throws SQLException {
        final Ticket table = new Ticket();
        final SetClauseList setList = table.name().set(dto.getName())
                                .also(table.description().set(dto.getDescription()));
        table.update(setList).where(table.projectId().eq(dto.getId())).execute(engine);
    }

    public TicketDto getTicketById(final Long ticketId) throws SQLException {
        final Ticket ticket = new Ticket();
        final List<TicketDto> list = new TicketSelect(ticket).where(ticket.id().eq(ticketId)).list(engine);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<RecordDto> getRecords(final Long ticketId) throws SQLException {
        Record record = new Record();
        return new RecordSelect(record).where(record.ticketId().eq(ticketId)).list(engine);
    }

    public void insertRecord(final Long ticketId, final String comment, final Long assigneeId, final Long editorId) throws SQLException {
        final Record record = new Record();
        final List<Long> keys = new ArrayList<>();
        final GeneratedKeys<Long> generatedKeys = GeneratedKeys.collect(record.id(), keys);
        record.insert(record.ticketId().set(ticketId).also(record.comment().set(comment)).also(record.assigneeId().set(assigneeId)).also(record.editorId().set(editorId)))
        .execute(generatedKeys, engine);
        final Ticket ticket = new Ticket();
        ticket.update(ticket.lastRecordId().set(keys.get(0)));
    }

}
