package edu.caltech.cs141b.collaborator.common;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Comment implements IsSerializable {

    private Date commentTime = null;
    private String commentBy = null;
    private String message = null;

    // Required by GWT serialization.
    public Comment() {

    }

    public Comment(Date commentTime, String commentBy, String message) {
        this.commentTime = commentTime;
        this.commentBy = commentBy;
        this.message = message;
    }

    public Date getCommentTime() {
        return this.commentTime;
    }

    public String getCommentBy() {
        return this.commentBy;
    }

    public String getMessage() {
        return this.message;
    }
}