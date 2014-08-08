/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.data;

import java.sql.Timestamp;

public class TicketDto {
    private final Long id;
    private String name;
    private String description;
    private final Timestamp creationdate;
    private final UsersDto author;
    private final ProjectDto project;
    private final UsersDto assignee;
    private final Boolean active;

    public TicketDto(final Long id, final Timestamp creationdate, final UsersDto author, final ProjectDto project, final UsersDto assignee, final Boolean active) {
        this.id = id;
        this.creationdate = creationdate;
        this.author = author;
        this.project = project;
        this.assignee = assignee;
        this.active = active;
    }

    public Long getId() {
    return id;
  }
    public String getName() {
    return name;
  }
    public void setName(final String name) {
    this.name = name;
  }

    public String getDescription() {
    return description;
  }
    public void setDescription(final String description) {
    this.description = description;
  }

    public Timestamp getCreationdate() {
    return creationdate;
  }

    public UsersDto getAuthor() {
        return author;
    }

    public ProjectDto getProject() {
        return project;
    }

    public UsersDto getAssignee() {
        return assignee;
    }

    public boolean isActive() {
        return active;
    }
}

