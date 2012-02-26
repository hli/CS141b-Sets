package edu.caltech.cs141b.collaborator.common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Document implements IsSerializable {

    private String key = null;
    private String title = null;
    private String contents = null;
    private Boolean locked = null;
    private Boolean isSimulate = null;

    // Required by GWT serialization.
    public Document() {

    }

    public Document(String key, String title, String contents, Boolean locked) {
        this.key = key;
        this.title = title;
        this.contents = contents;
        this.locked = locked;
        this.isSimulate = false;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContents() {
        return this.contents;
    }

    public boolean setTitle(String title) {
        if (this.isLocked()) {
            this.title = title;
            return true;
        }
        return false;
    }

    public boolean setContents(String contents) {
        if (this.isLocked()) {
            this.contents = contents;
            return true;
        }
        return false;
    }

    public Boolean isLocked() {
        return this.locked;
    }
    
    public Boolean getSimulate() {
        return this.isSimulate;
    }
    
    public void setSimulate() {
        this.isSimulate = true;
    }
}