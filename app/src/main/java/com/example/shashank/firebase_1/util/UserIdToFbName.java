package com.example.shashank.firebase_1.util;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class UserIdToFbName {
    public String fbName;

    public UserIdToFbName() {
        this.fbName = "";
    }

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
    }

    public void func(String userId) {

        final TaskCompletionSource<String> dbSource = new TaskCompletionSource<>();


        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("FbUserData").child(userId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String s = dataSnapshot.child("name").getValue(String.class);
//                setFbName(s);

                dbSource.setResult(s);

                Log.d("UserIdToFbName: ", fbName);

//                firebaseCallback.onCallback(fbName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Database Error 1", databaseError.getMessage());
            }
        };
        myRef.addValueEventListener(valueEventListener);

        Task<String> t = dbSource.getTask();

//        try{
//            Tasks.await(t, 100, TimeUnit.MILLISECONDS);
//        } catch(ExecutionException | InterruptedException | TimeoutException e) {
//            t = Tasks.forException(e);
//        }

        if(t.isSuccessful()){
            setFbName(t.getResult());
        }


//        readData(new FirebaseCallback() {
//            @Override
//            public void onCallback(String s) {
//                Log.d("UserIdToFbName2: ", s);
//            }
//        }, userId);
//
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        return this.fbName;

    }

//    private void readData(final FirebaseCallback firebaseCallback, String userId){
//
//        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("FbUserData").child(userId);
//
//
//        ValueEventListener valueEventListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                String s = dataSnapshot.child("name").getValue(String.class);
//                setFbName(s);
//
//                Log.d("UserIdToFbName: ", fbName);
//
//                firebaseCallback.onCallback(fbName);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("Database Error 1", databaseError.getMessage());
//            }
//        };
//        myRef.addValueEventListener(valueEventListener);
//
//    }
//
//    private interface FirebaseCallback {
//        void onCallback(String s);
//    }
}
