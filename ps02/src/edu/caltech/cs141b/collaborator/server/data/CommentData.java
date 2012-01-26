package edu.caltech.cs141b.collaborator.server.data;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class CommentData {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;

    @Persistent
    private DocumentData document;

    @Persistent
    private Date commentTime;

    @Persistent
    private String commentBy;

    @Persistent
    private String message;

    public CommentData(String message, String commentBy) {
        this.message = message;
        this.commentBy = commentBy;
        this.commentTime = new Date();
    }

    public String getKey() {
        return this.key;
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

    public DocumentData getDocument() {
        return this.document;
    }
}