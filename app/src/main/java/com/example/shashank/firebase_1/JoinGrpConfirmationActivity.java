package com.example.shashank.firebase_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shashank.firebase_1.data.FbUserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinGrpConfirmationActivity extends AppCompatActivity {

    TextView joinConfText1, joinConfText2, joinConfText3, joinConfText4;
    Button yesBtn, noBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseUser user;

    private FbUserInfo fbUserInfo;

    String groupName;
    String groupId;
    Map<String, String> groupAdmin;
    Map<String, Map<String, String>> groupMembers;
    List<String> grpMembersFbIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_grp_confirmation);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        fbUserInfo = new FbUserInfo();
        getUserFbInfo();

        Intent intent = getIntent();

        groupName = intent.getStringExtra("GROUPNAME");
        groupId = intent.getStringExtra("GROUPID");
        groupAdmin = new HashMap<String, String>();
        groupAdmin = (HashMap<String, String>)intent.getSerializableExtra("GROUPADMIN");
        groupMembers = new HashMap<>();
        groupMembers = (Map<String, Map<String, String>>) intent.getSerializableExtra("GROUPMEMBERS");

        grpMembersFbIds = new ArrayList<String>();
        for(Map<String, String> m: groupMembers.values()){
            for(String s: m.keySet()){
                grpMembersFbIds.add(s);
            }
        }

        Log.d("JoingrpFriendslist", grpMembersFbIds.get(0));

        joinConfText1 = (TextView) findViewById(R.id.joinConfText1);
        joinConfText2 = (TextView) findViewById(R.id.joinConfText2);
        joinConfText3 = (TextView) findViewById(R.id.joinConfText3);
        joinConfText4 = (TextView) findViewById(R.id.joinConfText4);
        yesBtn = (Button) findViewById(R.id.joingrpyesbtn);
        noBtn = (Button) findViewById(R.id.joingrpnobtn);

        joinConfText1.setText(groupName);
        joinConfText2.setText(groupId);

        StringBuilder temp2 = new StringBuilder();
        for(String s:groupAdmin.values()){
            temp2.append(s);
        }
        joinConfText3.setText(temp2);

        StringBuilder temp1 = new StringBuilder();
        for(Map<String, String> m: groupMembers.values()){
            for(String s: m.values()){
                temp1.append(s);
                temp1.append("\n");
            }
        }
        joinConfText4.setText(temp1);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = FirebaseDatabase.getInstance().getReference().child("UserGroups");

                Map<String, String> m1 = new HashMap<>();
                m1.put(fbUserInfo.getFbId(), fbUserInfo.getName());
                groupMembers.put(user.getUid(), m1);
                myRef.child(groupId).child("grpMembers").setValue(groupMembers);

                Toast.makeText(JoinGrpConfirmationActivity.this, "You have successfully joined the group "+groupName, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(JoinGrpConfirmationActivity.this, InformGrpMembersAfterJoining.class);
                intent.putExtra("FRIENDSLIST", (Serializable) grpMembersFbIds);
                startActivity(intent);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinGrpConfirmationActivity.this, JoinExistingGrpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUserFbInfo(){
        DatabaseReference myNewTempRef = FirebaseDatabase.getInstance().getReference().child("FbUserData").child(user.getUid());
        myNewTempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                fbUserInfo.setName(dataSnapshot.child("name").getValue(String.class));
                fbUserInfo.setFbId(dataSnapshot.child("fbId").getValue(String.class));


                Log.d("JoinGrpConfirmationAct", "JoinGrp: \n" + fbUserInfo.getFbId() + dataSnapshot.getValue());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
