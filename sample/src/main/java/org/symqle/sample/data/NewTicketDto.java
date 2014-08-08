/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.data;

public class NewTicketDto {
    private final String name;
    private final String description;
    private final Long projectId;

    public NewTicketDto(final String name, final String description, final Long projectId) {
        this.name = name;
        this.description = description;
        this.projectId = projectId;
    }

    public String getName() {
    return name;
  }
    public String getDescription() {
    return description;
  }

    public Long getProjectId() {
        return projectId;
    }
}

