package edu.caltech.cs141b.collaborator.common;

public class Message {
    public enum MessageType {
        AVAILABLE, UNAVAILABLE, EXPIRED
    }

    public MessageType type = null;
    public String docKey = null;
}