package edu.caltech.cs141b.collaborator.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.Serializer;

public class Document implements IsSerializable, JsonSerializable {

    private String key = null;
    private String title = null;
    private String contents = null;
    private Boolean locked = null;
    private Boolean simulate = null;

    // Required by GWT serialization.
    public Document() {

    }

    public Document(String key, String title, String contents, Boolean locked) {
        this.key = key;
        this.title = title;
        this.contents = contents;
        this.locked = locked;
        this.simulate = false;
    }

    public String getKey() {
        return this.key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getTitle() {
        return this.title;
    }
    public boolean setTitle(String title) {
        if (this.isLocked()) {
            this.title = title;
            return true;
        }
        return false;
    }
    public String getContents() {
        return this.contents;
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
    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
    public Boolean isSimulate() {
        return this.simulate;
    }
    public void setSimulate(Boolean sim) {
        this.simulate = sim;
    }
    
    /**
     * Convert json into Document object.
     *
     * @param JSON
     * @return Document object
     */
    public static Document fromJson(String json) {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        return (Document) serializer.deSerialize(json, "edu.caltech.cs141b.collaborator.common.Document");
    }
     
    /**
     * Creates a JSON string from the Document object.
     *
     * @return String of JSON
     */
    public String toJson() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        return serializer.serialize(this);
    }
}