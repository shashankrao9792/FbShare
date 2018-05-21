package com.example.shashank.firebase_1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareInGrpActivity extends AppCompatActivity {

    Button sharefilebtn, viewAllmsgsbtn, outsidelinkbtn;

    String groupName;
    String groupId;
    Map<String, String> groupAdmin;
    Map<String, Map<String, String>> groupMembers;
    List<String> grpMembersFbIds;
    String outsideUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_in_grp);

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
        outsideUrl = intent.getStringExtra("OUTSIDEURL");

        sharefilebtn = (Button) findViewById(R.id.fileUploadBtn);
        viewAllmsgsbtn = (Button) findViewById(R.id.viewAllShares);
        outsidelinkbtn = (Button) findViewById(R.id.shareOutsidelinkBtn);

        sharefilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(ShareInGrpActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    shiftToShareFileActivity();
                }
                else {
                    ActivityCompat.requestPermissions(ShareInGrpActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 6969);
                }
            }
        });

        viewAllmsgsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ShareInGrpActivity.this, ViewAllMsgsInGrp.class);
                intent1.putExtra("GROUPNAME", groupName);
                intent1.putExtra("GROUPID", groupId);
                intent1.putExtra("GROUPADMIN", (Serializable) groupAdmin);
                intent1.putExtra("GROUPMEMBERS", (Serializable) groupMembers);
                intent1.putExtra("FRIENDSLIST", (Serializable) grpMembersFbIds);
                startActivity(intent1);
            }
        });

        outsidelinkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(ShareInGrpActivity.this, ShareOutsideLinkUrlActivity.class);
                intent2.putExtra("GROUPNAME", groupName);
                intent2.putExtra("GROUPID", groupId);
                intent2.putExtra("GROUPADMIN", (Serializable) groupAdmin);
                intent2.putExtra("GROUPMEMBERS", (Serializable) groupMembers);
                intent2.putExtra("FRIENDSLIST", (Serializable) grpMembersFbIds);
                intent2.putExtra("OUTSIDEURL", outsideUrl);
                startActivity(intent2);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 6969 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            shiftToShareFileActivity();
        }
        else{
            Toast.makeText(ShareInGrpActivity.this, "Please provide permission to read external storage", Toast.LENGTH_SHORT).show();
        }

    }

    private void shiftToShareFileActivity() {

        Intent intent = new Intent(ShareInGrpActivity.this, UploadFileOntoStorageActivity.class);
        intent.putExtra("GROUPNAME", groupName);
        intent.putExtra("GROUPID", groupId);
        intent.putExtra("GROUPADMIN", (Serializable) groupAdmin);
        intent.putExtra("GROUPMEMBERS", (Serializable) groupMembers);
        intent.putExtra("FRIENDSLIST", (Serializable) grpMembersFbIds);
        startActivity(intent);
    }
}
