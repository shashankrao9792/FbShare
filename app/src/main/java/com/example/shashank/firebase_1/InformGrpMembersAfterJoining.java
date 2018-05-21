package com.example.shashank.firebase_1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.MessageDialog;

import java.util.ArrayList;
import java.util.List;

public class InformGrpMembersAfterJoining extends AppCompatActivity {

    CallbackManager callbackManager;
    Button msgBtn, noMsgBtn, continueBtn;
    MessageDialog messageDialog;

    List<String> peopleIds;

    static final String TAG = "InfmFrndsToJoinGrpAct: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_inform_grp_members_after_joining);

        Intent intent = getIntent();
        peopleIds = new ArrayList<String>();
        peopleIds = (List<String>) intent.getSerializableExtra("FRIENDSLIST");

        msgBtn = (Button)findViewById(R.id.button_inform_frnds_join_grp);
        noMsgBtn = (Button)findViewById(R.id.button_dont_inform_frnds_join_grp);
        continueBtn = (Button) findViewById(R.id.informFrndsJoinGroupBtn);
        continueBtn.setEnabled(false);

        callbackManager = CallbackManager.Factory.create();

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageDialog = new MessageDialog(InformGrpMembersAfterJoining.this);
                messageDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.e(TAG, "FB send success");
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "FB send cancelled");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e(TAG, "FB send error");
                    }
                });


                if (MessageDialog.canShow(ShareLinkContent.class)) {

                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setPeopleIds(peopleIds)
                            .setContentUrl(Uri.parse("https://drive.google.com/open?id=15WdPGkEqZLhWvlnuRaYrOFL1G0w42tbf"))
                            .build();

                    messageDialog.show(content);
                }

            }
        });

        noMsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgBtn.setEnabled(false);
                noMsgBtn.setEnabled(false);
                continueBtn.setEnabled(true);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMainActivity();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        msgBtn.setEnabled(false);
        noMsgBtn.setEnabled(false);
        continueBtn.setEnabled(true);
    }

    private void goToMainActivity(){
        Intent intent = new Intent(InformGrpMembersAfterJoining.this, MainActivity.class);
        startActivity(intent);
    }
}
