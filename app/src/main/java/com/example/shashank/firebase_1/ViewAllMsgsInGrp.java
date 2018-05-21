package com.example.shashank.firebase_1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shashank.firebase_1.model.ViewAllMsgsRecyclerClass;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Map;

public class ViewAllMsgsInGrp extends AppCompatActivity {

    private RecyclerView mGroupList;
    FirebaseRecyclerAdapter<ViewAllMsgsRecyclerClass, ViewAllMsgsRecyclerClassViewHolder> firebaseRecyclerAdapter;
    String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_msgs_in_grp);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("GROUPID");
    }


    @Override
    protected void onStart() {
        super.onStart();

        mGroupList = (RecyclerView) findViewById(R.id.viewAllMsgsInGrpRecycler);
        mGroupList.setHasFixedSize(true);
        mGroupList.setLayoutManager(new LinearLayoutManager(ViewAllMsgsInGrp.this));

        String groupToQuery = "gid_"+groupId;

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("FilesSharedUrl")
                .child("Files_Shared")
                .child(groupToQuery)
                .limitToLast(50);

        FirebaseRecyclerOptions<ViewAllMsgsRecyclerClass> options =
                new FirebaseRecyclerOptions.Builder<ViewAllMsgsRecyclerClass>()
                        .setQuery(query, ViewAllMsgsRecyclerClass.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ViewAllMsgsRecyclerClass, ViewAllMsgsRecyclerClassViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewAllMsgsRecyclerClassViewHolder holder, int position, @NonNull ViewAllMsgsRecyclerClass model) {
                holder.setMsgs(model.getFilename(), model.getUrl());
            }

            @NonNull
            @Override
            public ViewAllMsgsRecyclerClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_all_msgs, parent, false);

                return new ViewAllMsgsRecyclerClassViewHolder(view);
            }
        };

        mGroupList.setAdapter(firebaseRecyclerAdapter);


        firebaseRecyclerAdapter.startListening();
    }

    public static class ViewAllMsgsRecyclerClassViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ViewAllMsgsRecyclerClassViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        public void setMsgs(String key, String value){
            TextView msgText = (TextView) mView.findViewById(R.id.msgContent);

            msgText.setClickable(true);
            msgText.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='"+value+"'> "+key+" </a>";
            msgText.setText(Html.fromHtml(text));

        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }
}
