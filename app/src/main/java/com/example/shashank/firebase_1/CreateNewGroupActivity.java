package com.example.shashank.firebase_1;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.example.shashank.firebase_1.data.FbUserInfo;
import com.example.shashank.firebase_1.data.UserGroups;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CreateNewGroupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private FbUserInfo fbUserInfo;

    EditText newGroupName;
    Button newGroupCreate;

    PopupWindow popupWindow;
    Button okbtn ;
    ConstraintLayout constraintLayout;

    String maxGrpId;


    private static final String TAG = "CreateNewGroupAtivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();



        newGroupCreate = (Button) findViewById(R.id.createNewGroupBtn);
        newGroupName = (EditText) findViewById(R.id.newGroupName);

        fbUserInfo = new FbUserInfo();
        getUserFbInfo();
        getMaxGrpIdFromFRTDB();

        newGroupCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseDatabase = FirebaseDatabase.getInstance();
                myRef = mFirebaseDatabase.getReference();

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        UserGroups ug = new UserGroups();
                        String newGrpName = newGroupName.getText().toString();

                        // for the first group being created after app goes live
                        if(!(dataSnapshot.child("UserGroups").exists())){
                            insertFirstGrpIntoFRTDB();
                        }
                        else {

                            // shows popup window displaying error message if group name already exists
                            if(checkIfGrpNameExists(dataSnapshot, newGrpName)){
                                showPopupErrorMessage();
                            }
                            else{

                                addNewGroupForUser();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void insertFirstGrpIntoFRTDB(){
        UserGroups userGroups = new UserGroups();
        String newGrpName = newGroupName.getText().toString();

        userGroups.setGrpId("1");
        Map<String, String> gadmin = new HashMap<>();
        gadmin.put(user.getUid(), fbUserInfo.getName());
        userGroups.setGrpAdmin(gadmin);
        userGroups.setGrpName(newGrpName);
        userGroups.addSingleMember(user.getUid(), fbUserInfo.getFbId(), fbUserInfo.getName());

        myRef.child("UserGroups").child(userGroups.getGrpId()).setValue(userGroups);

        changeIntent();
    }

    private void getUserFbInfo(){
        DatabaseReference myNewTempRef = FirebaseDatabase.getInstance().getReference().child("FbUserData").child(user.getUid());
        myNewTempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fbUserInfo.setName(dataSnapshot.child("name").getValue(String.class));
                fbUserInfo.setFbId(dataSnapshot.child("fbId").getValue(String.class));


                Log.d(TAG, "newGroup: 13: \n" + fbUserInfo.getFbId() + dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private Boolean checkIfGrpNameExists(DataSnapshot dataSnapshot, String newGrpName){

        for(DataSnapshot ds:dataSnapshot.child("UserGroups").getChildren()){
            UserGroups tempUG = ds.getValue(UserGroups.class);
            if(tempUG.getGrpName().equals(newGrpName)){
//                Log.d(TAG, "newGroup: 4: \n" );
                return true;
            }
        }
        return false;
    }


    private void showPopupErrorMessage(){
        Log.d(TAG, "newGroup: 2: \n" );

        newGroupName.setEnabled(false);

        Context mContext = getApplicationContext();

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.group_name_exists_popup, null);

        okbtn = (Button) customView.findViewById(R.id.OKBtnForGrpNameExists);
        constraintLayout = (ConstraintLayout) findViewById(R.id.createNewGroupActivityXML);

        popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.showAtLocation(constraintLayout, Gravity.CENTER, 0, 0);

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupName.setText("");
                newGroupName.setEnabled(true);
                popupWindow.dismiss();
            }
        });
    }


    private void addNewGroupForUser(){
        Log.d(TAG, "newGroup: 3: \n" );

        UserGroups userGroups = new UserGroups();
        String newGrpName = newGroupName.getText().toString();

        userGroups.setGrpId(String.valueOf(Integer.parseInt(maxGrpId)+1));
        Map<String, String> gadmin = new HashMap<>();
        gadmin.put(user.getUid(), fbUserInfo.getName());
        userGroups.setGrpAdmin(gadmin);
        userGroups.setGrpName(newGrpName);
        userGroups.addSingleMember(user.getUid(), fbUserInfo.getFbId(), fbUserInfo.getName());

        myRef.child("UserGroups").child(userGroups.getGrpId()).setValue(userGroups);

        changeIntent();
    }

    // get max group id currently present in Firebase Realtime database
    private void getMaxGrpIdFromFRTDB(){
        Query myNewTempRef = FirebaseDatabase.getInstance().getReference().child("UserGroups").orderByChild("grpId").limitToLast(1);
        myNewTempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    maxGrpId = ds.getKey();
                }


                Log.d(TAG, "newGroup: 17: \n" + maxGrpId + dataSnapshot.getKey());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void changeIntent(){
        Intent intent = new Intent(CreateNewGroupActivity.this, InviteFriendsToNewGroup.class);
        startActivity(intent);
    }
}
