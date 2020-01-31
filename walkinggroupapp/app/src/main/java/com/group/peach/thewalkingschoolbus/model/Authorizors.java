package com.group.peach.thewalkingschoolbus.model;


/**Recreate object model for authorizor class under PermissionRequest*/
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.List;

public class Authorizors {
    private List<ObjectIDModel> parent;
    private WGServerProxy.PermissionStatus status;
    private ObjectIDModel whoApprovedOrDenied;

    public List<ObjectIDModel> getParent() {
        return parent;
    }

    public void setUsers(List<ObjectIDModel> users) {
        this.parent = users;
    }

    public WGServerProxy.PermissionStatus getStatus() {
        return status;
    }

    public void setStatus(WGServerProxy.PermissionStatus status) {
        this.status = status;
    }

    public ObjectIDModel getWhoApprovedOrDenied() {
        return whoApprovedOrDenied;
    }

    public void setWhoApprovedOrDenied(ObjectIDModel whoApprovedOrDenied) {
        this.whoApprovedOrDenied = whoApprovedOrDenied;
    }
}
