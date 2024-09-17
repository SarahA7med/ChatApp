package com.example.chatapp;

public class MessageModel {
    private String messageid;

    private  String senderid;

    private String message;

    private long timestamp;

    public MessageModel() {
    }

    public MessageModel(String messageid, String senderid, String message,long timestamp) {
        this.messageid = messageid;
        this.senderid = senderid;
        this.message = message;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageid() {
        return messageid;
    }

    public void setMessageid(String messageid) {
        this.messageid = messageid;
    }

    public String getSenderid() {
        return senderid;
    }

    public void setSenderid(String senderid) {
        this.senderid = senderid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
