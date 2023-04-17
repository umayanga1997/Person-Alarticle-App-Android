package com.openlab.personalarticle.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.openlab.personalarticle.Models.ArticleModel;
import com.openlab.personalarticle.R;
import com.openlab.personalarticle.utils.ProgressDialogCustom;

public class ArticleViewActivity extends AppCompatActivity {

    TextView writerTxt;
    TextView articleTxt;
    ImageView thumbImg;
    TextView contactLinkBtn;
    ProgressDialogCustom pdc;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        writerTxt = findViewById(R.id.writer_view_txt);
        articleTxt  = findViewById(R.id.article_view_txt);
        thumbImg = findViewById(R.id.thumbnail_view_img);
        contactLinkBtn = findViewById(R.id.contact_view_txt_link);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        pdc = new ProgressDialogCustom(this);

        Bundle extras = getIntent().getExtras();
        ArticleModel articleModel = (ArticleModel) extras.getSerializable("ARTICLE_DATA");
        writerTxt.setText(articleModel.getWriter());
        articleTxt.setText(articleModel.getArticle());
        Glide.with(this).load(articleModel.getThumbnailUrl()).centerCrop().into(thumbImg);

        setTitle("Article: " + articleModel.getTitle());

        contactLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdc.show();
                database.getReference("Users").child(articleModel.getUserID()).get()
                        .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                DataSnapshot snapshot = task.getResult();

                                pdc.dismiss();

                                if(snapshot.child("locLat").getValue() != null){
                                    Intent intent = new Intent(ArticleViewActivity.this
                                            , WriterLocationActivity.class);
                                    intent.putExtra("LONGITUDE", Double.parseDouble(snapshot
                                            .child("locLong").getValue().toString()));
                                    intent.putExtra("LATITUDE", Double.parseDouble(snapshot
                                            .child("locLat").getValue().toString()));
                                    intent.putExtra("ADDRESS", snapshot.child("address")
                                            .getValue().toString());
                                    intent.putExtra("MOBILE_NO", snapshot.child("mobileNo")
                                            .getValue().toString());
                                    startActivity(intent);
                                }

                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ArticleViewActivity.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        pdc.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}