package edu.caltech.cs141b.collaborator.common;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentRevision extends Document implements IsSerializable {

	private Date updatedTime = null;
	private String updatedBy = null;

	// Required by GWT serialization.
	public DocumentRevision() {
		super();
	}
	
	public DocumentRevision(String key, String title, String contents,
			Date updatedTime, String updatedBy) {
		super(key, title, contents, true);
		this.updatedTime = updatedTime;
		this.updatedBy = updatedBy;
	}
	
	public Date getUpdatedTime() {
		return this.updatedTime;
	}
	
	public String getUpdatedBy() {
		return this.updatedBy;
	}
}