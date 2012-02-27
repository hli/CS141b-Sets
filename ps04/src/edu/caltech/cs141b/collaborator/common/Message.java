package edu.caltech.cs141b.collaborator.common;

import com.google.gwt.core.client.GWT;
import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.Serializer;

public class Message implements JsonSerializable {
    
    public enum MessageType {
        AVAILABLE, UNAVAILABLE, CHECKEDOUT, EXPIRED
    }

    private MessageType type = null;
    private String docKey = null;
    private int position = -1;
    private String docContents = null;
    private String docTitle = null;
    private Boolean simulate = null;
    
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
    public String getDocContents() {
        return this.docContents;
    }
    public void setDocContents(String docContents) {
        this.docContents= docContents;
    }
    public String getDocTitle() {
        return this.docTitle;
    }
    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }
    public Boolean isSimulate() {
        return this.simulate;
    }
    public void setSimulate(Boolean sim) {
        this.simulate = sim;
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
     * A constructor with three parameters, used only for sending CHECKEDOUT messages.
     */
    public Message(String dKey, String docTitle, String docContents, Boolean simulate) {
        super();
        this.type = Message.MessageType.CHECKEDOUT;
        this.docKey = dKey;
        this.docTitle = docTitle;
        this.docContents = docContents;
        this.simulate = simulate;
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