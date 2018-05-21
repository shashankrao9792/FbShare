package com.example.shashank.firebase_1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.shashank.firebase_1.data.FilesSharedUrl;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareOutsideLinkUrlActivity extends AppCompatActivity {

    String groupName;
    String groupId;
    Map<String, String> groupAdmin;
    Map<String, Map<String, String>> groupMembers;
    List<String> grpMembersFbIds;
    String outsideUrl;

    FilesSharedUrl filesSharedUrlDB;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    TextView outurl;
    Button yBtn, nBtn, cBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_share_outside_link_url);

        Intent intent = getIntent();
        groupName = intent.getStringExtra("GROUPNAME");
        groupId = intent.getStringExtra("GROUPID");
        groupAdmin = new HashMap<String, String>();
        groupAdmin = (HashMap<String, String>)intent.getSerializableExtra("GROUPADMIN");
        groupMembers = new HashMap<>();
        groupMembers = (Map<String, Map<String, String>>) intent.getSerializableExtra("GROUPMEMBERS");
        grpMembersFbIds = new ArrayList<>();
        grpMembersFbIds = (ArrayList<String>) intent.getSerializableExtra("FRIENDSLIST");
        outsideUrl = intent.getStringExtra("OUTSIDEURL");

        yBtn = (Button) findViewById(R.id.yBtn);
        nBtn = (Button) findViewById(R.id.nBtn);
        cBtn = (Button) findViewById(R.id.cBtn);
        cBtn.setEnabled(false);

        outurl = (TextView) findViewById(R.id.outURL);
        outurl.setText(outsideUrl);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        yBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String dirName = "gid_"+groupId;
                filesSharedUrlDB = new FilesSharedUrl(outsideUrl, outsideUrl);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("FilesSharedUrl");
                ref.child("Files_Shared").child(dirName).child("OutsideURL").setValue(filesSharedUrlDB);

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setQuote("Check out this link I shared on "+groupName)
                        .setContentUrl(Uri.parse(outsideUrl))
                        .setPeopleIds(grpMembersFbIds)
                        .build();
                if(ShareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(linkContent);
                }

//                Toast.makeText(FirstTimeLoginActivity.this, "Posted on FB!", Toast.LENGTH_SHORT).show();

                yBtn.setEnabled(false);
                nBtn.setEnabled(false);
                cBtn.setEnabled(true);
            }
        });

        nBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yBtn.setEnabled(false);
                nBtn.setEnabled(false);
                cBtn.setEnabled(true);
            }
        });

        cBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    private void goToMainActivity(){
        Intent intent = new Intent(ShareOutsideLinkUrlActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
