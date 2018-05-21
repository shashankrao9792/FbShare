package com.example.shashank.firebase_1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedFilesPostOnFB extends AppCompatActivity {

    String groupName;
    String groupId;
    Map<String, String> groupAdmin;
    Map<String, Map<String, String>> groupMembers;
    List<String> grpMembersFbIds;
    Map<String, String> filesSharedURLs;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    Button continueBtn, exitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        Intent intent = getIntent();
        groupName = intent.getStringExtra("GROUPNAME");
        groupId = intent.getStringExtra("GROUPID");
        groupAdmin = new HashMap<String, String>();
        groupAdmin = (HashMap<String, String>)intent.getSerializableExtra("GROUPADMIN");
        groupMembers = new HashMap<>();
        groupMembers = (Map<String, Map<String, String>>) intent.getSerializableExtra("GROUPMEMBERS");
        grpMembersFbIds = new ArrayList<>();
        grpMembersFbIds = (ArrayList<String>) intent.getSerializableExtra("FRIENDSLIST");
        filesSharedURLs = new HashMap<>();
        filesSharedURLs = (HashMap<String, String>) intent.getSerializableExtra("SHAREDPOSTSURL");

//        String pdfUrl = filesSharedURLs.get("pdf")!=null ?filesSharedURLs.get("pdf") :"";
//        String docUrl = filesSharedURLs.get("doc")!=null ?filesSharedURLs.get("doc") :"";
//        String imgUrl = filesSharedURLs.get("image")!=null ?filesSharedURLs.get("image") :"";
//        String audioUrl = filesSharedURLs.get("audio")!=null ?filesSharedURLs.get("audio") :"";
//        String videoUrl = filesSharedURLs.get("video")!=null ?filesSharedURLs.get("video") :"";

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        if(filesSharedURLs.get("pdf")!=null){
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(filesSharedURLs.get("pdf")))
                    .setPeopleIds(grpMembersFbIds)
                    .setQuote("FbShare Message: Check out this PDF I have shared on "+groupName+"!!")
                    .build();

            if(ShareDialog.canShow(ShareLinkContent.class)){
                shareDialog.show(linkContent);
            }
        }
        else if(filesSharedURLs.get("doc")!=null){
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(filesSharedURLs.get("doc")))
                    .setPeopleIds(grpMembersFbIds)
                    .setQuote("FbShare Message: Check out this DOC I have shared on "+groupName+"!!")
                    .build();

            if(ShareDialog.canShow(ShareLinkContent.class)){
                shareDialog.show(linkContent);
            }
        }
        else if(filesSharedURLs.get("image")!=null){
//            try {

            new AsyncTask<String, Void, Bitmap>(){

                @Override
                protected Bitmap doInBackground(String... urls) {
//                    String url = urls[0];
                    URL url = null;
                    try {
                        url = new URL(urls[0]);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    Bitmap mIcon11 = null;
                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.connect();
                        InputStream input = connection.getInputStream();
                        mIcon11 = BitmapFactory.decodeStream(input);
                    } catch (Exception e) {
                        Log.e("Error for image ", e.getMessage());
                        e.printStackTrace();
                        mIcon11 = null;
                    }
                    return mIcon11;
                }

                @Override
                protected void onPostExecute(Bitmap image){
                    SharePhoto photo = new SharePhoto.Builder()
                            .setBitmap(image)
                            .build();
                    SharePhotoContent content = new SharePhotoContent.Builder()
                            .addPhoto(photo)
                            .setPeopleIds(grpMembersFbIds)
                            .setRef("FbShare Message: Check out this PIC I have shared on "+groupName+"!!")
                            .build();
                    if(ShareDialog.canShow(ShareLinkContent.class)){
                        shareDialog.show(content);
                    }
                }
            }.execute(filesSharedURLs.get("image"));
//                Bitmap image = getBitmapFromURL(filesSharedURLs.get("image"));


//            } catch(IOException e) {
//                System.out.println(e);
//            }

        }
        else if(filesSharedURLs.get("audio")!=null){
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(filesSharedURLs.get("audio")))
                    .setPeopleIds(grpMembersFbIds)
                    .setQuote("FbShare Message: Check out this MUSIC file I have shared on "+groupName+"!!")
                    .build();

            if(ShareDialog.canShow(ShareLinkContent.class)){
                shareDialog.show(linkContent);
            }
        }
        else if(filesSharedURLs.get("video")!=null){
            ShareVideo video = new ShareVideo.Builder()
                    .setLocalUrl(Uri.parse(filesSharedURLs.get("video")))
                    .build();
            ShareVideoContent content = new ShareVideoContent.Builder()
                    .setVideo(video)
                    .setPeopleIds(grpMembersFbIds)
                    .setContentDescription("FbShare Message: Check out this VIDEO I have shared on "+groupName+"!!")
                    .build();
            if(ShareDialog.canShow(ShareLinkContent.class)){
                shareDialog.show(content);
            }
        }

        setContentView(R.layout.activity_shared_files_post_on_fb);

        continueBtn = (Button) findViewById(R.id.anotherPostBtn);
        exitBtn = (Button) findViewById(R.id.mainScreenButton);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SharedFilesPostOnFB.this, UploadFileOntoStorageActivity.class);
                intent1.putExtra("GROUPNAME", groupName);
                intent1.putExtra("GROUPID", groupId);
                intent1.putExtra("GROUPADMIN", (Serializable) groupAdmin);
                intent1.putExtra("GROUPMEMBERS", (Serializable) groupMembers);
                intent1.putExtra("FRIENDSLIST", (Serializable) grpMembersFbIds);
                startActivity(intent1);
            }
        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(SharedFilesPostOnFB.this, MainActivity.class);
                startActivity(intent2);
            }
        });
    }
}
