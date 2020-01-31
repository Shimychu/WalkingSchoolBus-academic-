package com.group.peach.thewalkingschoolbus.model;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)

public class User {
    private long id;
    private String name;
    private String password;
    private String email;
    private Long birthYear;
    private String birthMonth;
    private String address;
    private String homePhone;
    private String cellPhone;
    private String grade;
    private String teacherName;
    private String emergencyContactInfo;
    private Long totalPointsEarned;
    private EarnedRewards rewards;


    private List<User> parents = new ArrayList<>();
    private List<User> children = new ArrayList<>();

    private List<ObjectIDModel> monitoroedByUsers = new ArrayList<>();
    private List<ObjectIDModel> monitorsUsers = new ArrayList<>();
    private List<ObjectIDModel> memberOfGroups = new ArrayList<>();

    private String userToken;


    //default constructor for testing only
    private User() {
    }

    //name, email constructor
    private User(String name, String email){
        this.name = name;
        this.email = email;
    }

    //singleton support
    private static User instance;
    public static User getInstance(){
        if (instance == null){
            instance = new User();
        }
        return instance;
    }
    public static void setUser(User user){
        instance=user;
    }

    public static void clearUser(){
        instance = new User();
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    //getters
    public long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<User> getChildren() {
        return children;
    }

    public List<User> getParents() {
        return parents;
    }

    public List<User> getAllContact() {
        List<User> allContact = new ArrayList<>();
        allContact.addAll(parents);
        allContact.addAll(children);

        Collections.sort(allContact, new CompareByUserName());
        for (User user : allContact){
            Log.d("user class", "has email: " + user.getEmail());
        }
        return allContact;
    }

    //getters for updated server
    public Long getBirthYear() { return birthYear;}

    public String getBirthMonth() { return birthMonth;}

    public String getAddress() { return address;}

    public String getHomePhone() { return homePhone;}

    public String getCellPhone() { return cellPhone;}

    public String getGrade() { return grade;}

    public String getTeacherName() { return teacherName;}

    public String getEmergencyContactInfo() {
        return emergencyContactInfo;
    }

    public boolean isChild(User targetUser){
        for (User eachUser : children){
            if (targetUser.getEmail().equals(eachUser.getEmail())){
                return true;
            }
        }
        return false;
    }

    //setters
    public void setId(long id) {
        this.id = id;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

    public void setParents(List<User> parents) {
        this.parents = parents;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void addParent(User user){
        parents.add(user);
    }

    public void addChild(User user){
        children.add(user);
    }

    //setters for updated server
    public void setBirthYear(Long birthYear) { this.birthYear = birthYear; }

    public void setBirthMonth(String birthMonth) { this.birthMonth = birthMonth; }

    public void setAddress(String address) { this.address = address;}

    public void setHomePhone(String homePhone) { this.homePhone = homePhone;}

    public void setCellPhone(String cellPhone) { this.cellPhone = cellPhone;}

    public void setGrade(String grade) {this.grade = grade;}

    public void setTeacherName(String teacherName) {this.teacherName = teacherName;}

    public void setEmergencyContactInfo(String emergencyContactInfo) {this.emergencyContactInfo = emergencyContactInfo;}

    //remove
    public void removeContactByID(long targetID){
        for (int i = 0; i < parents.size(); i++) {
            if (targetID == parents.get(i).id){
                parents.remove(i);
            }
        }
        for (int i = 0; i < children.size(); i++) {
            if (targetID == children.get(i).id){
                children.remove(i);
            }
        }
    }

    public List<ObjectIDModel> getMonitoroedByUsers() {
        return monitoroedByUsers;
    }

    public void setMonitoroedByUsers(List<ObjectIDModel> monitoroedByUsers) {
        this.monitoroedByUsers = monitoroedByUsers;
    }

    public List<ObjectIDModel> getMonitorsUsers() {
        return monitorsUsers;
    }

    public void setMonitorsUsers(List<ObjectIDModel> monitorsUsers) {
        this.monitorsUsers = monitorsUsers;
    }

    public List<ObjectIDModel> getMemberOfGroups() {
        return memberOfGroups;
    }

    public void setMemberOfGroups(List<ObjectIDModel> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }

    public Long getTotalPointsEarned() {
        return totalPointsEarned;
    }

    public void setTotalPointsEarned(Long totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    public EarnedRewards getRewards() {
        return rewards;
    }

    public void setRewards(EarnedRewards rewards) {
        this.rewards = rewards;
    }

    // Setter will be called when deserializing User's JSON object; we'll automatically
    // expand it into the custom object.
    public void setCustomJson(String jsonString) {
        Log.w("USER", "De-serializing string: " + jsonString);
        try {
            rewards = new ObjectMapper().readValue(
                    jsonString,
                    EarnedRewards.class);
            Log.w("USER", "De-serialized embedded rewards object: " + rewards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Having a getter will make this function be called to set the value of the
    // customJson field of the JSON data being sent to server.
    public String getCustomJson() {
        // Convert custom object to a JSON string:
        String customAsJson = null;
        try {
            customAsJson = new ObjectMapper().writeValueAsString(rewards);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return customAsJson;
    }


    public static class CompareByUserName implements Comparator<User> {
        @Override
        public int compare(User user1, User user2) {
            return user1.getName().compareTo(user2.getName());
        }
    }

    public static class CompareUserByScore implements Comparator<User> {
        @Override
        public int compare(User user1, User user2) {

            if(user1.getTotalPointsEarned()==null||user2.getTotalPointsEarned()==null) {
                return 0;
            }

            return user1.getTotalPointsEarned().compareTo(user2.getTotalPointsEarned());
        }
    }

}

