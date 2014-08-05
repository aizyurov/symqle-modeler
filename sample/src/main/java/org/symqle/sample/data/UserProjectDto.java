/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.data;


public class UserProjectDto {
    private final Long userId;
    private final Long projectId;

   public UserProjectDto(final Long projectId,final Long userId) {
            this.projectId = projectId;
            this.userId = userId;
   }

  public Long getUserId() {
    return userId;
  }
  public Long getProjectId() {
    return projectId;
  }
}

