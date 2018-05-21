package com.example.shashank.firebase_1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

public class FirstTimeLoginActivity extends AppCompatActivity {

    Button yesBtn, noBtn, continueBtn;
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_first_time_login);

        yesBtn = (Button) findViewById(R.id.firstTimeMsgYesBtn);
        noBtn = (Button) findViewById(R.id.firstTimeMsgNoBtn);
        continueBtn = (Button) findViewById(R.id.firstTimeMsgContinueBtn);
        continueBtn.setEnabled(false);

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                                                    .setQuote("Download the app using this link!")
                                                    .setContentUrl(Uri.parse("https://play.google.com"))
                                                    .build();
                if(ShareDialog.canShow(ShareLinkContent.class)){
                    shareDialog.show(linkContent);
                }

                Toast.makeText(FirstTimeLoginActivity.this, "Posted on FB!", Toast.LENGTH_SHORT).show();

                yesBtn.setEnabled(false);
                noBtn.setEnabled(false);
                continueBtn.setEnabled(true);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yesBtn.setEnabled(false);
                noBtn.setEnabled(false);
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

    private void goToMainActivity(){
        Intent intent = new Intent(FirstTimeLoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
