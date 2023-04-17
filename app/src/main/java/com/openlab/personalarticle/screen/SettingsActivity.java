package com.openlab.personalarticle.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.openlab.personalarticle.LoginActivity;
import com.openlab.personalarticle.R;
import com.openlab.personalarticle.utils.ProgressDialogCustom;

public class SettingsActivity extends AppCompatActivity {

    FirebaseDatabase database;
    FirebaseAuth auth;

    Button deleteBtn;

    ProgressDialogCustom pdc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        deleteBtn = findViewById(R.id.acc_delete_btn);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        pdc = new ProgressDialogCustom(this);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pdc.show();
                database.getReference().child("Users").child(auth.getCurrentUser().getUid())
                        .setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(
                                            GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(
                                                    getString(R.string.default_web_client_id))
                                            .requestEmail()
                                            .build();
                                    GoogleSignInClient gsc = GoogleSignIn.getClient(
                                            SettingsActivity.this, gso);
                                    gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingsActivity.this,
                                                        "Account was deleted successfully!",
                                                        Toast.LENGTH_LONG).show();
                                                pdc.dismiss();
                                                navigate();
                                            }
                                        }
                                    });

                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SettingsActivity.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        pdc.dismiss();
                    }
                });
            }
        });
    }

    private void navigate(){
        Intent mainIntent = new Intent(SettingsActivity.this, LoginActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(mainIntent);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}