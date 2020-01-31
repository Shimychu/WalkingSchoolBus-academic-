package com.group.peach.thewalkingschoolbus.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 3/14/2018.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    private Long id;
    private String groupDescription;
    private Leader leader;

    private List<Double> routeLatArray = new ArrayList<>();
    private List<Double> routeLngArray = new ArrayList<>();
    private List<ObjectIDModel> memberUsers = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    private String href;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public List<Double> getRouteLatArray() {
        return routeLatArray;
    }

    public void setRouteLatArray(List<Double> routeLatArray) {
        this.routeLatArray = routeLatArray;
    }

    public List<Double> getRouteLngArray() {
        return routeLngArray;
    }

    public void setRouteLngArray(List<Double> routeLngArray) {
        this.routeLngArray = routeLngArray;
    }

    public List<ObjectIDModel> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(List<ObjectIDModel> memberUsers) {
        this.memberUsers = memberUsers;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Leader getLeader() {
        return leader;
    }

    public void setLeader(Leader leader) {
        this.leader = leader;
    }

    public void addRouteLatLng(double latitude, double longitude) {
        routeLatArray.add(latitude);
        routeLngArray.add(longitude);
    }

    public boolean hasLocation(){
        return(!(routeLatArray.isEmpty() || routeLngArray.isEmpty()));
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}