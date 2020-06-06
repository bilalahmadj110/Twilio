package com.bilalahmad.twilio;

public class Model {
    private int responseId;
    private String sender, receiver;
    private String responseMsg;

    public Model(int responseId, String sender, String receiver, String responseMsg) {
        this.responseId = responseId;
        this.sender = sender;
        this.receiver = receiver;
        this.responseMsg = responseMsg;
    }

    public int getResponseId() {
        return responseId;
    }

    public void setResponseId(int responseId) {
        this.responseId = responseId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }


}
