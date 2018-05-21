package com.example.shashank.firebase_1.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserGroups {

    String grpId;
    String grpName;
    Map<String, String> grpAdmin;

    Map<String, Map<String, String>> grpMembers;

    public UserGroups() {
        grpId = "";
        grpName = "";
        grpAdmin = new HashMap<>();
        grpMembers = new HashMap<>();
    }

    public Map<String, Map<String, String>> getGrpMembers() {
        return grpMembers;
    }

    public void setGrpMembers(Map<String, Map<String, String>> grpMembers) {
        this.grpMembers = grpMembers;
    }

    public String getGrpId() {
        return grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }

    public String getGrpName() {
        return grpName;
    }

    public void setGrpName(String grpName) {
        this.grpName = grpName;
    }

    public Map<String, String> getGrpAdmin() {
        return grpAdmin;
    }

    public void setGrpAdmin(Map<String, String> grpAdmin) {
        this.grpAdmin = grpAdmin;
    }



    public void addSingleMember(String userId, String fbId, String name){
//        grpMembersDetails g = new grpMembersDetails(fbId, name);
        Map<String, String> s = new HashMap<>();
        s.put(fbId, name);
        this.grpMembers.put(userId, s);
    }

}
