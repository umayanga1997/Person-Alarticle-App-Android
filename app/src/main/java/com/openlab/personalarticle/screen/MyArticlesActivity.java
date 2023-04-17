package com.openlab.personalarticle.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.openlab.personalarticle.Models.ArticleModel;
import com.openlab.personalarticle.R;
import com.openlab.personalarticle.databinding.ActivityHomeBinding;
import com.openlab.personalarticle.databinding.ActivityMyArticlesBinding;
import com.openlab.personalarticle.utils.ArticleViewAdapter;
import com.openlab.personalarticle.utils.ArticleViewAdapterPrivate;
import com.openlab.personalarticle.utils.ProgressDialogCustom;

import java.util.ArrayList;

public class MyArticlesActivity extends AppCompatActivity {

    ArrayList<ArticleModel> articleList;
    ArticleViewAdapterPrivate adapter;
    RecyclerView recyclerView;
    ProgressDialogCustom pdc;
    FirebaseAuth auth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_articles);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdc = new ProgressDialogCustom(this);
        pdc.show();

        recyclerView = findViewById(R.id.my_articles_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Articles");

        database.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articleList = new ArrayList<ArticleModel>();
                for (DataSnapshot  snap : snapshot.getChildren()){
                    ArticleModel article = snap.getValue(ArticleModel.class);
                    article.setId(snap.getKey());
                    article.setUserID(auth.getCurrentUser().getUid());
                    articleList.add(article);
                }

                adapter = new ArticleViewAdapterPrivate(MyArticlesActivity.this, articleList);
                recyclerView.setAdapter(adapter);
                pdc.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pdc.dismiss();
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