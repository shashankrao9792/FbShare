package com.example.shashank.firebase_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    Button newGroupBtn, joinExistingGrpBtn, viewExistingGrpBtn, logoutBtn;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        newGroupBtn = (Button)findViewById(R.id.button2);
        joinExistingGrpBtn = (Button)findViewById(R.id.button3);
        viewExistingGrpBtn = (Button)findViewById(R.id.button4);
        logoutBtn = (Button)findViewById(R.id.button5);

        newGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase analytics
                FirebaseAnalytics fa = FirebaseAnalytics.getInstance(MainActivity.this);
                Bundle eventDetails = new Bundle();
                eventDetails.putString("new_group_msg", "Clicked new group button");
                fa.logEvent("new_group_event", eventDetails);

                Intent intent = new Intent(MainActivity.this, CreateNewGroupActivity.class);
                startActivity(intent);
            }
        });

        joinExistingGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase analytics
                FirebaseAnalytics fa = FirebaseAnalytics.getInstance(MainActivity.this);
                Bundle eventDetails = new Bundle();
                eventDetails.putString("join_existing_group", "Clicked join existing group button");
                fa.logEvent("join_existing_group_event", eventDetails);

                Intent intent = new Intent(MainActivity.this, JoinExistingGrpActivity.class);
                startActivity(intent);
            }
        });

        viewExistingGrpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase analytics
                FirebaseAnalytics fa = FirebaseAnalytics.getInstance(MainActivity.this);
                Bundle eventDetails = new Bundle();
                eventDetails.putString("view_existing_group", "Clicked view existing group button");
                fa.logEvent("view_existing_group_event", eventDetails);

                Intent intent = new Intent(MainActivity.this, ViewExistingGroupsActivity.class);
                intent.setAction(Intent.ACTION_MAIN);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Firebase analytics
                FirebaseAnalytics fa = FirebaseAnalytics.getInstance(MainActivity.this);
                Bundle eventDetails = new Bundle();
                eventDetails.putString("logout_msg", "Clicked logout button");
                fa.logEvent("logout_event", eventDetails);


                mAuth.signOut();
                LoginManager.getInstance().logOut();
                updateUI();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            updateUI();
        }




    }

    private void updateUI() {
        Toast.makeText(MainActivity.this, "Logging out!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this, FacebookLoginActivity.class);
        startActivity(intent);
        finish();
    }
}
