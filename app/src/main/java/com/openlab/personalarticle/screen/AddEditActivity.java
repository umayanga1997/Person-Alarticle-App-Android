package com.openlab.personalarticle.screen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openlab.personalarticle.LoginActivity;
import com.openlab.personalarticle.Models.ArticleModel;
import com.openlab.personalarticle.R;
import com.openlab.personalarticle.utils.ProgressDialogCustom;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddEditActivity extends AppCompatActivity {

    TextInputEditText titleTxt;
    TextInputEditText articleTxt;
    ShapeableImageView thumbnailImageBtn;
    Button savePublic;
    Button savePrivate;

    FirebaseAuth auth;
    FirebaseDatabase database;
    StorageReference storageReference;

    Uri imageUri, imageUriEdit = null;
    ArticleModel articleModel;

    ProgressDialogCustom pdc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("images/");

        pdc = new ProgressDialogCustom(this);

        titleTxt = findViewById(R.id.title_txt);
        articleTxt = findViewById(R.id.article_txt);
        thumbnailImageBtn = findViewById(R.id.thumbnail_img_btn);
        savePrivate = findViewById(R.id.save_private_btn);
        savePublic = findViewById(R.id.save_public_btn);

        Bundle extras = getIntent().getExtras();
        articleModel = (ArticleModel) extras.getSerializable("ARTICLE_DATA");

        if(articleModel != null){
            titleTxt.setText(articleModel.getTitle());
            articleTxt.setText(articleModel.getArticle());
            imageUriEdit = Uri.parse(articleModel.getThumbnailUrl());
            Glide.with(this).load(articleModel.getThumbnailUrl()).centerCrop()
                    .into(thumbnailImageBtn);
            setTitle("Edit Article");
        }

        thumbnailImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        savePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(false);
            }
        });
        savePrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(true);
            }
        });

    }

    private void saveData(boolean isPrivate)  {

        if(titleTxt.getText() == null || titleTxt.getText().toString().equals("") ){
            Toast.makeText(AddEditActivity.this, "Please enter the Title",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if(articleTxt.getText() == null || articleTxt.getText().toString().equals("")){
            Toast.makeText(AddEditActivity.this, "Please enter the Article Description",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(imageUriEdit == null && imageUri == null){
            Toast.makeText(AddEditActivity.this, "Please Select a Thumbnail",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        pdc.show();

        if(imageUri != null) {
            // Delete image when image is exist
            if(imageUriEdit != null){
                deleteExistImageAndSaveData(isPrivate, imageUriEdit.toString());
            }else{
                uploadImageAndSaveData(isPrivate);
            }
        }else{
            saveDataDB(isPrivate, imageUriEdit.toString());
        }
    }

    private void deleteExistImageAndSaveData(boolean isPrivate, String url) {
            final StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
            ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    uploadImageAndSaveData(isPrivate);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pdc.dismiss();
                    Toast.makeText(AddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                }
            });
    }

    private void uploadImageAndSaveData(boolean isPrivate){
        // File Name
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",
                Locale.ENGLISH);
        Date now = new Date();
        String fileName = formatter.format(now);

        final StorageReference ref = storageReference.child(fileName);

        ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>
                () {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Task<Uri> urlTask = task.continueWithTask(new Continuation<UploadTask
                            .TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                                throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                saveDataDB(isPrivate, task.getResult().toString());
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pdc.dismiss();
                            Toast.makeText(AddEditActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pdc.dismiss();
                Toast.makeText(AddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
    private void saveDataDB(boolean isPrivate, String url) {
        // UserId, Username
        String userId = auth.getCurrentUser().getUid();
        String userName = auth.getCurrentUser().getDisplayName();
        // Article ID
        String id;
        if(articleModel != null){
            id = articleModel.getId();
        }else{
           id = UUID.randomUUID().toString();
        }

        // Mapped Data
        ArticleModel article = new ArticleModel();
        article.setTitle(titleTxt.getText().toString());
        article.setArticle(articleTxt.getText().toString());
        article.setPrivateArticle(isPrivate);
        article.setThumbnailUrl(url);
        article.setCreateDate(new Date());
        article.setLastUpdateDate(new Date());
        article.setWriter(userName);

        database.getReference("Articles").child(userId).child(id).setValue(article)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    pdc.dismiss();
                    Toast.makeText(AddEditActivity.this, "Article data was saved " +
                            "successful!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pdc.dismiss();
                Toast.makeText(AddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(articleModel != null)
            getMenuInflater().inflate(R.menu.add_edit_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            deleteArticle();
        }
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteArticle() {
        String userId = auth.getCurrentUser().getUid();
        pdc.show();
        database.getReference("Articles").child(userId).child(articleModel.getId())
                .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                final StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(
                        articleModel.getThumbnailUrl());
                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pdc.dismiss();
                        Toast.makeText(AddEditActivity.this, "Article data was deleted " +
                                "successful!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pdc.dismiss();
                        Toast.makeText(AddEditActivity.this,"Raw data was deleted " +
                                "successful!, But Thumbnail image not deleted :: "+ e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pdc.dismiss();
                Toast.makeText(AddEditActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100 && data != null && data.getData() != null){
            imageUri = data.getData();
            thumbnailImageBtn.setImageURI(imageUri);
        }
    }

}