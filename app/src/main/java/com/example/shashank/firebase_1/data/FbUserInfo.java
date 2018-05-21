package com.example.shashank.firebase_1.data;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FbUserInfo {

    String fbId = "";
    String name = "";
    String email = "";
    String gender = "";

    Map<String, String> friends;

    public FbUserInfo() {
        friends = new HashMap<String, String>();
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Map<String, String> getFriends() {
        return friends;
    }

    public void setFriends(Map<String, String> friends) {
        this.friends = friends;
    }

    public void addSingleFriend(String fbId, String name){
        this.friends.put(fbId, name);
    }

}
