package edu.caltech.cs141b.collaborator.server.data;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class DocumentRevisionData {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
    private String key;

    @Persistent
    private DocumentData document;

    @Persistent
    private Date updatedTime;

    @Persistent
    private String updatedBy;

    @Persistent
    private Text contents;

    public DocumentRevisionData(String contents, String updatedBy) {
        this.contents = new Text(contents);
        this.updatedBy = updatedBy;
        this.updatedTime = new Date();
    }

    public String getKey() {
        return this.key;
    }

    public Date getUpdatedTime() {
        return this.updatedTime;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public String getContents() {
        return this.contents.getValue();
    }

    public DocumentData getDocument() {
        return this.document;
    }
}