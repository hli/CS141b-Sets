package edu.caltech.cs141b.collaborator.common;

import com.google.gwt.core.client.JavaScriptObject;

public class Message extends JavaScriptObject {
    public enum MessageType {
        AVAILABLE, UNAVAILABLE, EXPIRED
    }

    private MessageType type = null;
    private String docKey = null;
    private int position = -1;
    
    public final native MessageType getType() /*-{ 
        return this.type;
    }-*/;
    
    public final native void setType(MessageType value) /*-{
        this.type = value;
    }-*/;
    
    public final native String getDocKey() /*-{
        return this.docKey;
    }-*/;
    
    public final native void setDocKey(String value) /*-{
        this.docKey = value;
    }-*/;
    
    public static final native Message buildMessage(String json) /*-{
        return eval('(' + json + ')');
    }-*/;
    
    public final native int getPosition() /*-{
        return this.position;
    }-*/;
    
    public final native void setPosition(int position) /*-{
        this.position = position;
    }-*/;

}