package edu.caltech.cs141b.collaborator.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DocumentHeader implements IsSerializable {

	private String key = null;
	private String title = null;
	
	// Required by GWT serialization.
	public DocumentHeader() {
		
	}
	
	public DocumentHeader(String key, String title) {
		this.key = key;
		this.title = title;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public String getTitle() {
		return this.title;
	}
}
