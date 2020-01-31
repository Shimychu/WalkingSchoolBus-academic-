package com.group.peach.thewalkingschoolbus.model;


import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.ArrayList;
import java.util.List;

/**match the format of PermissionRequest JSON object
 * recreate the object model that are being handed back from the server*/
public class PermissionRequest {
    private long id;
    private String action;
    private WGServerProxy.PermissionStatus status;
    private ObjectIDModel userA;
    private ObjectIDModel userB;
    private ObjectIDModel groupG;
    private ObjectIDModel requestingUser;
    private List<Authorizors> authorizors = new ArrayList<>();
    private String message;
    private String href;


    //getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public WGServerProxy.PermissionStatus getStatus() {
        return status;
    }

    public void setStatus(WGServerProxy.PermissionStatus status) {
        this.status = status;
    }

    public ObjectIDModel getUserA() {
        return userA;
    }

    public void setUserA(ObjectIDModel userA) {
        this.userA = userA;
    }

    public ObjectIDModel getUserB() {
        return userB;
    }

    public void setUserB(ObjectIDModel userB) {
        this.userB = userB;
    }

    public ObjectIDModel getGroupG() {
        return groupG;
    }

    public void setGroupG(ObjectIDModel groupG) {
        this.groupG = groupG;
    }

    public ObjectIDModel getRequestingUser() {
        return requestingUser;
    }

    public void setRequestingUser(ObjectIDModel requestingUser) {
        this.requestingUser = requestingUser;
    }

    public List<Authorizors> getAuthorizors() {
        return authorizors;
    }

    public void setAuthorizors(List<Authorizors> authorizors) {
        this.authorizors = authorizors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
