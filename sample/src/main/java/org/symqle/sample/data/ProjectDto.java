/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.data;


public class ProjectDto {
    private final Long id;
    private String name;

   public ProjectDto(final Long id) {
            this.id = id;
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

}

