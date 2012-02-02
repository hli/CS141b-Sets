package edu.caltech.cs141b.collaborator.server.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class DocumentData {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;

    @Persistent
    private String title;

    @Persistent
    private String lockedBy;

    @Persistent
    private Date lockedUntil;

    @Persistent(mappedBy = "document")
    @Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "updatedTime desc"))
    private List<DocumentRevisionData> revisions = new ArrayList<DocumentRevisionData>();

    @Persistent(mappedBy = "document")
    @Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "commentTime desc"))
    private List<CommentData> comments = new ArrayList<CommentData>();

    public DocumentData(String title, String contents, String updatedBy,
            String lockedBy, Date lockedUntil) {
        this.title = title;
        this.revisions.add(new DocumentRevisionData(contents, updatedBy));
        this.lockedBy = lockedBy;
        this.lockedUntil = lockedUntil;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitle() {
        return this.title;
    }

    public String getLockedBy() {
        return this.lockedBy;
    }

    public Date getLockedUntil() {
        return this.lockedUntil;
    }

    public String getContents() {
        return this.revisions.get(0).getContents();
    }

    public List<DocumentRevisionData> getRevisions() {
        return this.revisions;
    }
    
    public List<CommentData> getComments() {
        return this.comments;
    }

    public List<CommentData> getComments(int start, int end) {
        return this.comments.subList(start,
                Math.min(end, this.comments.size()));
    }

    public Integer getNumComments() {
        return this.comments.size();
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setContents(String contents, String updatedBy) {
        this.revisions.add(0, new DocumentRevisionData(contents, updatedBy));
    }

    public void addComment(String message, String commentBy) {
        this.comments.add(0, new CommentData(message, commentBy));
    }

    public void lock(String lockedBy, Date lockedUntil) {
        this.lockedBy = lockedBy;
        this.lockedUntil = lockedUntil;
    }

    public void unlock() {
        this.lockedBy = null;
        this.lockedUntil = null;
    }
}