package edu.caltech.cs141b.collaborator.server.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

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
    
    @Persistent
    private Boolean isSimulate;

    @Persistent(mappedBy = "document")
    @Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "updatedTime desc"))
    private List<DocumentRevisionData> revisions = new ArrayList<DocumentRevisionData>();

    @Persistent(mappedBy = "document")
    @Order(extensions = @Extension(vendorName = "datanucleus", key = "list-ordering", value = "commentTime asc"))
    private List<CommentData> comments = new ArrayList<CommentData>();
    
    @Persistent
    private Vector<String> queue = new Vector<String>();

    public DocumentData(String title, String contents, String updatedBy,
            String lockedBy, Date lockedUntil) {
        this.title = title;
        this.revisions.add(new DocumentRevisionData(contents, updatedBy));
        this.lockedBy = lockedBy;
        this.lockedUntil = lockedUntil;
        this.isSimulate = false;
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
    
    public void addToQueue(String clientId) {
        this.queue.add(clientId);
    }
    
    public void popFromQueue() {
        this.queue.remove(0);
    }
    
    public void removeFromQueue(String clientId) {
        this.queue.remove(clientId);
    }
    
    public String peekAtQueue() {
        return this.queue.firstElement();
    }
    
    public Boolean queueContains(String clientId) {
        return this.queue.contains(clientId);
    }
    
    public Integer indexInQueue(String clientId) {
        return this.queue.indexOf(clientId);
    }
    
    public Boolean queueIsEmpty() {
        return this.queue.isEmpty();
    }

    public Boolean getSimulate() {
        return this.isSimulate;
    }
    
    public void setSimulate() {
        this.isSimulate = true;
    }
}