package com.bilalahmad.twilio;

import org.json.JSONException;
import org.json.JSONObject;

public class Model {
    private int id;
    private String sender, receiver, speechMsg, status;
    private String responseMsg, SID;

    public Model() {
    }

    public Model(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getInt("id");
        sender = jsonObject.getString("from_ph_num");
        receiver = jsonObject.getString("to_ph_num");
        status = jsonObject.getString("status");
        speechMsg = jsonObject.getString("speech_text");
        responseMsg = jsonObject.getString("response");
        SID = jsonObject.getString("sid");
    }

    public Model(int id, String sender, String receiver, String speechMsg, String status, String responseMsg, String sid) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.speechMsg = speechMsg;
        this.status = status;
        this.responseMsg = responseMsg;
        this.SID = sid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getSpeechMsg() {
        return speechMsg;
    }

    public void setSpeechMsg(String speechMsg) {
        this.speechMsg = speechMsg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getSID() {
        return SID;
    }

    public void setSID(String SID) {
        this.SID = SID;
    }
}
