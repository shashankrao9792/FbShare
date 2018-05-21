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

import com.example.shashank.firebase_1.model.ViewGrpRecyclerClass;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ViewExistingGroupsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
//    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseUser user;
//
//    String currUserFbId;
//    Map<String, List<String>> grpsWhichUserIsPartof;
//    List<String> fbIdsOfGrpMembers;

    private RecyclerView mGroupList;
    FirebaseRecyclerAdapter<ViewGrpRecyclerClass, ViewGrpRecyclerClassViewHolder> firebaseRecyclerAdapter;

    static String outsideUrl;

    private Map<String, String> friends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_existing_groups);

        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        if(receivedAction.equals(Intent.ACTION_MAIN)){
            //app has been launched directly, not from share list
            outsideUrl = "";
        }
        else if(receivedAction.equals(Intent.ACTION_SEND)){
            //content is being shared
            if(receivedType.startsWith("text/")){
                //handle sent text
                outsideUrl = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }
        Log.d("OutsideURL: ", outsideUrl);

    }



    @Override
    protected void onStart() {
        super.onStart();

//        myRef = FirebaseDatabase.getInstance().getReference().child("UserGroups");
//        myRef.keepSynced(true);

        mGroupList = (RecyclerView) findViewById(R.id.viewGrpRecyclerView);
        mGroupList.setHasFixedSize(true);
        mGroupList.setLayoutManager(new LinearLayoutManager(ViewExistingGroupsActivity.this));

        Log.d("ViewExistingGroupsAct: ", "At 1");


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("UserGroups")
                .limitToLast(50);

        FirebaseRecyclerOptions<ViewGrpRecyclerClass> options =
                new FirebaseRecyclerOptions.Builder<ViewGrpRecyclerClass>()
                        .setQuery(query, ViewGrpRecyclerClass.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ViewGrpRecyclerClass, ViewGrpRecyclerClassViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewGrpRecyclerClassViewHolder holder, int position, @NonNull ViewGrpRecyclerClass model) {
                holder.setGrpName(model.getGrpName());
                holder.setGrpAdmin(model.getGrpAdmin());
                holder.setGrpId(model.getGrpId());
                holder.setGrpMembers(model.getGrpMembers());
            }

            @NonNull
            @Override
            public ViewGrpRecyclerClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_group_row, parent, false);
                return new ViewGrpRecyclerClassViewHolder(view);
            }
        };

        mGroupList.setAdapter(firebaseRecyclerAdapter);


        firebaseRecyclerAdapter.startListening();
    }

    public static class ViewGrpRecyclerClassViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        View mView;
        String groupId;
        String groupName;
        Map<String, String> groupAdmin;
        Map<String, Map<String, String>> groupMembers;
        public ViewGrpRecyclerClassViewHolder(View itemView){
            super(itemView);
            groupId = "";
            groupName = "";
            groupAdmin = new HashMap<>();
            groupMembers = new HashMap<>();
            itemView.setOnClickListener(this);
            mView = itemView;
        }
        public void setGrpName(String grpName){
            TextView txtgrpName = (TextView) mView.findViewById(R.id.cardGrpNameView);
            txtgrpName.setText(grpName);
            groupName = grpName;
        }
        public void setGrpMembers(Map<String, Map<String, String>> grpMembers){
            TextView txtgrpMembers = (TextView) mView.findViewById(R.id.cardGrpMembersView);
            StringBuilder dispGrpMembers = new StringBuilder();


            for(Map<String, String> m: grpMembers.values()){
                for(String s: m.values()){
                    dispGrpMembers.append(s);
                    dispGrpMembers.append("\n");
                }
            }


            Log.d("returnfromutil", dispGrpMembers.toString());
            txtgrpMembers.setText(dispGrpMembers);
            groupMembers = grpMembers;
        }

        public void setGrpId(String gid){
            groupId = gid;
        }

        public void setGrpAdmin(Map<String, String> gadmin) {groupAdmin = gadmin;}

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            Intent intent = new Intent(context, ShareInGrpActivity.class);
            intent.putExtra("GROUPNAME", groupName);
            intent.putExtra("GROUPID", groupId);
            intent.putExtra("GROUPADMIN", (Serializable) groupAdmin);
            intent.putExtra("GROUPMEMBERS", (Serializable) groupMembers);
            intent.putExtra("OUTSIDEURL", outsideUrl);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
