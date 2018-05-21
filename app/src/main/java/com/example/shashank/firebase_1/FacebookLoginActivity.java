package com.example.shashank.firebase_1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.shashank.firebase_1.data.FbIdToUserIdMap;
import com.example.shashank.firebase_1.data.FbUserInfo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FacebookLoginActivity extends AppCompatActivity {

    private CallbackManager mCallbackManager;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseUser user;


    private Button mFacebookBtn;

    private static final String TAG = "FacebookLoginActivity";

    private FbUserInfo currentFbUserInfo;
    private FbIdToUserIdMap fbidtouid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        mFacebookBtn = (Button) findViewById(R.id.button_facebook_login);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        mFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mFacebookBtn.setEnabled(false);

                LoginManager.getInstance().logInWithReadPermissions(FacebookLoginActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);

                        currentFbUserInfo = new FbUserInfo();

                        fillFbUserClass(loginResult.getAccessToken());
                        fillFbUserClassWithFriends(loginResult.getAccessToken());

                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "facebook:onError", error);
                        // ...
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void fillFbUserClass(AccessToken token){

        user = mAuth.getCurrentUser();

        GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try{
                    Log.d(TAG, "inside user profile request"+object.toString());
                    currentFbUserInfo.setFbId(object.getString("id"));
                    currentFbUserInfo.setName(object.getString("name"));
                    currentFbUserInfo.setEmail(object.getString("email"));
                    currentFbUserInfo.setGender(object.getString("gender"));

                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("fields", "id,name,email,gender");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    private void fillFbUserClassWithFriends(AccessToken token){

        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(token, "/me/friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Log.d("fillUserFbFriendsClas: ", response.toString());

                try {

                    JSONArray dataArray = response.getJSONObject().getJSONArray("data");
                    for(int i=0; i<dataArray.length(); i++){
                        JSONObject tempjson = dataArray.getJSONObject(i);
                        String friendId = tempjson.getString("id");
                        String friendName = tempjson.getString("name");
                        currentFbUserInfo.addSingleFriend(friendId, friendName);
                    }

                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        graphRequest.executeAsync();
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateFRTDBwithFBData();
                            updateFRTDBwithMappings();
                            mFacebookBtn.setEnabled(true);
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            mFacebookBtn.setEnabled(true);
                            updateUI();
                        }
                    }
                });
    }


    private void updateFRTDBwithFBData(){

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        //read from database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                Log.d(TAG, "onDataChange: Added info to database: \n" + dataSnapshot.getValue());

                checkForFirstTimeLogin(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }

    private void checkForFirstTimeLogin(DataSnapshot dataSnapshot){

        user = mAuth.getCurrentUser();
        String userId = user.getUid();

        if(!(dataSnapshot.child("FbUserData").child(userId).exists())){
            myRef.child("FbUserData").child(userId).setValue(currentFbUserInfo);
//            Toast.makeText(FacebookLoginActivity.this, "Added FB data in Firebase", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "checkforfirsttimelogin:  \n" + dataSnapshot.child(userId) +"\n"+ dataSnapshot.child("FbUserData").child(userId).exists());

            Intent intent = new Intent(FacebookLoginActivity.this, FirstTimeLoginActivity.class);
            startActivity(intent);
        }

        myRef.child("FbUserData").child(userId).setValue(currentFbUserInfo);
//        Toast.makeText(FacebookLoginActivity.this, "Updated FB data in Firebase", Toast.LENGTH_SHORT).show();
    }

    private void updateFRTDBwithMappings(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        user = mAuth.getCurrentUser();
        String userId = user.getUid();

        fbidtouid = new FbIdToUserIdMap();
        Map<String, String > m = new HashMap<>();
        m.put(currentFbUserInfo.getFbId(), userId);
        fbidtouid.setMappings(m);

        myRef.child("FbIdToUserIdMap").child(currentFbUserInfo.getName()).setValue(fbidtouid);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            updateUI();
        }

    }

    private void updateUI() {

        Intent intent = new Intent(FacebookLoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
