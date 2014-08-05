/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.data;


public class UsersDto {
    private final Long id;
    private String name;
    private String email;

   public UsersDto(final Long id) {
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

  public String getEmail() {
    return email;
  }
  public void setEmail(final String email) {
    this.email = email;
  }

}

