package com.example.shashank.firebase_1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shashank.firebase_1.data.FilesSharedUrl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadFileOntoStorageActivity extends AppCompatActivity {

    private static final int PDF_REQUEST_CODE = 1;
    private static final int DOC_REQUEST_CODE = 2;
    private static final int IMAGE_REQUEST_CODE = 3;
    private static final int AUDIO_REQUEST_CODE = 4;
    private static final int VIDEO_REQUEST_CODE = 5;

    Uri pdfUri, docUri, imgUri, audioUri, videoUri;

    String groupName;
    String groupId;
    Map<String, String> groupAdmin;
    Map<String, Map<String, String>> groupMembers;
    List<String> grpMembersFbIds;

    Button pdfBtn, docBtn, imgBtn, audioBtn, videoBtn, shareBtn, fbpostBtn;
    TextView fileNames;
    StringBuilder fileNamesS;
    String pdfFileName, docFileName, imgFileName, audioFileName, videoFileName;

    Map<String, String> filesSharedURLs;

    ProgressDialog progressDialog;

    FilesSharedUrl filesSharedUrlDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file_onto_storage);

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

        pdfBtn = (Button) findViewById(R.id.sharePdfBtn);
        docBtn = (Button) findViewById(R.id.shareDocBtn);
        imgBtn = (Button) findViewById(R.id.shareImageBtn);
        audioBtn = (Button) findViewById(R.id.shareAudioBtn);
        videoBtn = (Button) findViewById(R.id.shareVideoBtn);
        shareBtn = (Button) findViewById(R.id.uploadToFS);
        shareBtn.setEnabled(false);
        fbpostBtn = (Button) findViewById(R.id.shareOnFB);
        fbpostBtn.setEnabled(false);

//        fileNames = (TextView) findViewById(R.id.fileNamesSelected);
        fileNamesS = new StringBuilder();
        fileNamesS.append("The following files have been selected:\n");

        pdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdf();
            }
        });

        docBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDoc();
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        audioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAudio();
            }
        });

        videoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVideo();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfUri==null && docUri==null && imgUri==null && audioUri==null && videoUri==null){
                    Toast.makeText(UploadFileOntoStorageActivity.this, "Please select a file to upload", Toast.LENGTH_SHORT).show();
                }
                else{
                    uploadFiles();

                    shareBtn.setEnabled(false);

                    fbpostBtn.setEnabled(true);
                }

            }
        });

        fbpostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shiftIntents();
            }
        });
    }

    private void shiftIntents(){
        Intent intent = new Intent(UploadFileOntoStorageActivity.this, SharedFilesPostOnFB.class);
        intent.putExtra("GROUPNAME", groupName);
        intent.putExtra("GROUPID", groupId);
        intent.putExtra("GROUPADMIN", (Serializable) groupAdmin);
        intent.putExtra("GROUPMEMBERS", (Serializable) groupMembers);
        intent.putExtra("FRIENDSLIST", (Serializable) grpMembersFbIds);
        intent.putExtra("SHAREDPOSTSURL", (Serializable) filesSharedURLs);
        startActivity(intent);
    }

    private void selectPdf(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, PDF_REQUEST_CODE);
    }

    private void selectDoc(){
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, DOC_REQUEST_CODE);
    }

    private void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, IMAGE_REQUEST_CODE);
    }

    private void selectAudio(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, AUDIO_REQUEST_CODE);
    }

    private void selectVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            pdfUri = data.getData();
            pdfFileName = data.getData().getLastPathSegment();
            fileNamesS.append(pdfFileName+"\t");
        }
        else if(requestCode == DOC_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            docUri = data.getData();
            docFileName = data.getData().getLastPathSegment();
            fileNamesS.append(docFileName+"\t");
        }
        else if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgUri = data.getData();
            imgFileName = data.getData().getLastPathSegment();
            fileNamesS.append(imgFileName+"\t");
        }
        else if(requestCode == AUDIO_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            audioUri = data.getData();
//            filesSharedURLs.put("audio", audioUri.toString());

            audioFileName = data.getData().getLastPathSegment();
            fileNamesS.append(audioFileName+"\t");
        }
        else if(requestCode == VIDEO_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            videoUri = data.getData();
            filesSharedURLs.put("video", videoUri.toString());

            videoFileName = data.getData().getLastPathSegment();
            fileNamesS.append(videoFileName+"\t");
        }
        else{
            Toast.makeText(UploadFileOntoStorageActivity.this, "Please select a file to share!", Toast.LENGTH_SHORT).show();
        }

        pdfBtn.setEnabled(false);
        docBtn.setEnabled(false);
        imgBtn.setEnabled(false);
        audioBtn.setEnabled(false);
        videoBtn.setEnabled(false);

        shareBtn.setEnabled(true);
//        fileNames.setText(fileNamesS);
    }

    private void uploadFiles(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Uploading Files...");
        progressDialog.show();

        final String dirName = "gid_"+groupId;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(dirName);

        if(pdfUri!=null){
            storageReference.child("PDF_Files").child(pdfFileName).putFile(pdfUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    filesSharedURLs.put("pdf",url);
                                    filesSharedUrlDB = new FilesSharedUrl(pdfFileName, url);

//                                    Map<String, String> toBeStored = new HashMap<>();
//                                    toBeStored.put(pdfFileName, url);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FilesSharedUrl");
                                    databaseReference.child("Files_Shared").child(dirName).child(pdfFileName).setValue(filesSharedUrlDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setTitle("Uploading PDF File...");
                    int currProgress = (int) (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currProgress);
                }
            });
        }

        if(docUri!=null){
            storageReference.child("DOCX_Files").child(docFileName).putFile(docUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    filesSharedURLs.put("doc", url);
                                    filesSharedUrlDB = new FilesSharedUrl(docFileName, url);

//                                    Map<String, String> toBeStored = new HashMap<>();
//                                    toBeStored.put(docFileName, url);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FilesSharedUrl");
                                    databaseReference.child("Files_Shared").child(dirName).child(docFileName).setValue(filesSharedUrlDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setTitle("Uploading DOCX File...");
                    int currProgress = (int) (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currProgress);
                }
            });
        }

        if(imgUri!=null){
            storageReference.child("Image_Files").child(imgFileName).putFile(imgUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    filesSharedURLs.put("image", url);
                                    filesSharedUrlDB = new FilesSharedUrl(imgFileName, url);

//                                    Map<String, String> toBeStored = new HashMap<>();
//                                    toBeStored.put(imgFileName, url);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FilesSharedUrl");
                                    databaseReference.child("Files_Shared").child(dirName).child(imgFileName).setValue(filesSharedUrlDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setTitle("Uploading Image File...");
                    int currProgress = (int) (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currProgress);
                }
            });
        }

        if(audioUri!=null){
            storageReference.child("Audio_Files").child(audioFileName).putFile(audioUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    filesSharedURLs.put("audio", url);
                                    filesSharedUrlDB = new FilesSharedUrl(audioFileName, url);

//                                    Map<String, String> toBeStored = new HashMap<>();
//                                    toBeStored.put(audioFileName, url);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FilesSharedUrl");
                                    databaseReference.child("Files_Shared").child(dirName).child(audioFileName).setValue(filesSharedUrlDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setTitle("Uploading Audio File...");
                    int currProgress = (int) (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currProgress);
                }
            });
        }

        if(videoUri!=null){
            storageReference.child("Video_Files").child(videoFileName).putFile(videoUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
//                                    filesSharedURLs.put("video", url);
                                    filesSharedUrlDB = new FilesSharedUrl(videoFileName, url);

//                                    Map<String, String> toBeStored = new HashMap<>();
//                                    toBeStored.put(videoFileName, url);
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("FilesSharedUrl");
                                    databaseReference.child("Files_Shared").child(dirName).child(videoFileName).setValue(filesSharedUrlDB).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(UploadFileOntoStorageActivity.this, "File not uploaded: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.setTitle("Uploading Video File...");
                    int currProgress = (int) (100 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setProgress(currProgress);
                }
            });
        }
    }
}
