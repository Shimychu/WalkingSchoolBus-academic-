package com.group.peach.thewalkingschoolbus.model;

/**store information of one specific message*/
public class Message {

    private Long id;
    private Long timestamp;
    private String text;
    private ObjectIDModel fromUser;
    private ObjectIDModel toGroup;
    private boolean emergency;
    private String href;

    public Message(){}

    public Message(String text, boolean emergency) {
        this.id = id;
        this.timestamp = 0L;
        this.text = text;
        this.emergency = emergency;
        this.href = "";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ObjectIDModel getFromUser() {
        return fromUser;
    }

    public void setFromUser(ObjectIDModel fromUser) {
        this.fromUser = fromUser;
    }

    public ObjectIDModel getToGroup() {
        return toGroup;
    }

    public void setToGroup(ObjectIDModel toGroup) {
        this.toGroup = toGroup;
    }

    public boolean getEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
