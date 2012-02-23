package edu.caltech.cs141b.collaborator.common;

import com.google.gwt.core.client.GWT;
import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.Serializer;

public class Message implements JsonSerializable {
    
    public enum MessageType {
        AVAILABLE, UNAVAILABLE, EXPIRED
    }

    private MessageType type = null;
    private String docKey = null;
    private int position = -1;
    
    public MessageType getType() {
        return this.type;
    }
    public void setType(MessageType mType) {
        this.type = mType;
    }
    public String getDocKey() {
        return this.docKey;
    }
    public void setDocKey(String dKey) {
        this.docKey = dKey;
    }
    public int getPosition() {
        return this.position;
    }
    public void setPosition(int pos) {
        this.position = pos;
    }
    
    /**
     * A constructor with no parameters is required
     */
    public Message() {
        super();
    }
    
    /**
     * A constructor with three parameters
     */
    public Message(MessageType mType, String dKey, int pos) {
        super();
        this.type = mType;
        this.docKey = dKey;
        this.position = pos;
    }
    
    /**
     * Convert json into Message object.
     *
     * @param JSON
     * @return Message object
     */
    public static Message fromJson(String json) {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        return (Message) serializer.deSerialize(json, "edu.caltech.cs141b.collaborator.common.Message");
    }
     
    /**
     * Creates a JSON string from the Message object.
     *
     * @return String of JSON
     */
    public String toJson() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        return serializer.serialize(this);
    }
}