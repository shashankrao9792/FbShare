package com.example.shashank.firebase_1;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.shashank.firebase_1.model.JoinGrpRecyclerClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class JoinExistingGrpActivity extends AppCompatActivity {

    private RecyclerView mGroupList;
    FirebaseRecyclerAdapter<JoinGrpRecyclerClass, JoinGrpRecyclerClassViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_existing_grp);

    }

    @Override
    protected void onStart(){
        super.onStart();

//        myRef = FirebaseDatabase.getInstance().getReference().child("UserGroups");
//        myRef.keepSynced(true);

        mGroupList = (RecyclerView) findViewById(R.id.joinGrpRecyclerView);
        mGroupList.setHasFixedSize(true);
        mGroupList.setLayoutManager(new LinearLayoutManager(JoinExistingGrpActivity.this));

        Log.d("JoinExistingGrpAct: ", "At 1");


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("UserGroups")
                .limitToLast(50);

        FirebaseRecyclerOptions<JoinGrpRecyclerClass> options =
                new FirebaseRecyclerOptions.Builder<JoinGrpRecyclerClass>()
                        .setQuery(query, JoinGrpRecyclerClass.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<JoinGrpRecyclerClass, JoinGrpRecyclerClassViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull JoinGrpRecyclerClassViewHolder holder, int position, @NonNull JoinGrpRecyclerClass model) {
                holder.setGrpName(model.getGrpName());
                holder.setGrpAdmin(model.getGrpAdmin());
                holder.setGrpId(model.getGrpId());
                holder.setGrpMembers(model.getGrpMembers());
            }

            @NonNull
            @Override
            public JoinGrpRecyclerClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_group_row, parent, false);

                return new JoinGrpRecyclerClassViewHolder(view);
            }
        };

        mGroupList.setAdapter(firebaseRecyclerAdapter);


        firebaseRecyclerAdapter.startListening();
    }

    public static class JoinGrpRecyclerClassViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        View mView;
        String groupId;
        String groupName;
        Map<String, String> groupAdmin;
        Map<String, Map<String, String>> grpMembers;
        public JoinGrpRecyclerClassViewHolder(View itemView){
            super(itemView);
            groupId = "";
            groupName = "";
            groupAdmin = new HashMap<>();
            grpMembers = new HashMap<>();
            itemView.setOnClickListener(this);
            mView = itemView;
        }
        public void setGrpName(String grpName){
            TextView txtgrpName = (TextView) mView.findViewById(R.id.cardGrpNameJoin);
            txtgrpName.setText(grpName);
            groupName = grpName;
        }
        public void setGrpAdmin(Map<String, String> grpAdmin){
            TextView txtgrpAdminName = (TextView) mView.findViewById(R.id.cardGrpAdminJoin);
            for(String s:grpAdmin.values()){
                txtgrpAdminName.setText(s);
            }
            groupAdmin = grpAdmin;
        }
        public void setGrpId(String gid){
            groupId = gid;
        }
        public void setGrpMembers(Map<String, Map<String, String>> gmem){
            grpMembers = gmem;
        }

        @Override
        public void onClick(View v) {
            Log.d("Onclickofcard: ", groupId+" "+grpMembers.values().toString()+"   "+groupName+" "+groupAdmin.values().toString());

            Context context = v.getContext();
            Intent intent = new Intent(context, JoinGrpConfirmationActivity.class);
            intent.putExtra("GROUPNAME", groupName);
            intent.putExtra("GROUPID", groupId);
            intent.putExtra("GROUPADMIN", (Serializable) groupAdmin);
            intent.putExtra("GROUPMEMBERS", (Serializable) grpMembers);
            context.startActivity(intent);
        }
    }

//    @Override
//    protected void onStop(){
//        super.onStop();
//        firebaseRecyclerAdapter.stopListening();
//    }
//
//    String adminName = "";
//    private String getNamefromUid(String uid){
//        DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("FbUserData").child(uid);
//
//        d.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                adminName = dataSnapshot.child("name").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        return adminName;
//    }

//    private void getListOfUserFriends(){
//
//
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("FbUserData").child(user.getUid());
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                currFbUserInfo.setName(dataSnapshot.child("name").getValue(String.class));
//                currFbUserInfo.setFbId(dataSnapshot.child("fbId").getValue(String.class));
//                currFbUserInfo.setFriends((Map<String, String>) dataSnapshot.child("friends").getValue());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        myRef.addListenerForSingleValueEvent(valueEventListener);
//    }
//
//    private void convertFriendsFbIdsToUserIds(){
//
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("FbIdToUserIdMap");
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//
//
//                Log.d("JoinExistingGrpAct: ", "1 "+currFbUserInfo.getFriends().values().toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
//
//        myRef.addListenerForSingleValueEvent(valueEventListener);
//
//    }
}
