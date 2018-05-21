package com.example.shashank.firebase_1;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookDialog;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.internal.ShareDialogFeature;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMessengerGenericTemplateContent;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.MessageDialog;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendsToNewGroup extends AppCompatActivity {

    CallbackManager callbackManager;
    Button msgBtn, noMsgBtn, continueBtn;
    MessageDialog messageDialog;

    static final String TAG = "InvFrndsToNewGrpAct: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_invite_friends_to_new_group);

        msgBtn = (Button)findViewById(R.id.button_invite_frnds_new_grp);
        noMsgBtn = (Button)findViewById(R.id.button_dont_invite_frnds_new_grp);
        continueBtn = (Button) findViewById(R.id.inviteFrndsNewGroupBtn);
        continueBtn.setEnabled(false);

        callbackManager = CallbackManager.Factory.create();

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageDialog = new MessageDialog(InviteFriendsToNewGroup.this);
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
                                                        .setContentUrl(Uri.parse("https://drive.google.com/open?id=19FQ-h7TP2Dh02WTKh0AozSOBykbcMU2b"))
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
        Intent intent = new Intent(InviteFriendsToNewGroup.this, MainActivity.class);
        startActivity(intent);
    }
}
