package org.symqle.sample.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvovich
 */
public class ProjectWithUsersDto extends ProjectDto {

    private final List<UsersDto> users;

    public ProjectWithUsersDto(final Long id) {
        super(id);
        this.users = new ArrayList<>();
    }

    public List<UsersDto> getUsers() {
        return new ArrayList<>(users);
    }

    public void setUsers(final List<UsersDto> users) {
        this.users.clear();
        this.users.addAll(users);
    }

}
