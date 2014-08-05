/* THIS IS SAMPLE CODE. SAVE AND EDIT AS NECESSARY */

package org.symqle.sample.data;


import org.symqle.sample.data.UsersDto;

public class RecordDto {
    private final String comment;
    private final UsersDto editor;
    private final UsersDto assignee;
    private final Long active;

    public RecordDto(final String comment, final UsersDto editor, final UsersDto assignee, final Long active) {
        this.comment = comment;
        this.editor = editor;
        this.assignee = assignee;
        this.active = active;
    }

    public String getComment() {
        return comment;
    }

    public UsersDto getEditor() {
        return editor;
    }

    public UsersDto getAssignee() {
        return assignee;
    }

    public Long getActive() {
        return active;
    }
}

