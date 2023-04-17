package com.openlab.personalarticle.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.openlab.personalarticle.R;
import com.openlab.personalarticle.utils.ProgressDialogCustom;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    FusedLocationProviderClient flp;
    private static final int REQUEST_CODE = 101;
    Button saveBtn;
    RelativeLayout findLocationBtn;
    Location currentLocation;
    private Address geoCoder;

    FirebaseAuth auth;
    FirebaseDatabase database;

    TextView latTxt;
    TextView longTxt;
    TextView addressTxt;
    ImageButton mapBtn;
    TextInputEditText mobileNoTxt;

    ProgressDialogCustom pdc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        flp = LocationServices.getFusedLocationProviderClient(this);
        longTxt = findViewById(R.id.long_txt);
        latTxt = findViewById(R.id.lat_txt);
        addressTxt = findViewById(R.id.address_txt);
        addressTxt = findViewById(R.id.address_txt);
        saveBtn = findViewById(R.id.save_details_btn);
        mobileNoTxt = findViewById(R.id.mobile_no_txt);
        mapBtn = findViewById(R.id.map_btn);
        findLocationBtn = findViewById(R.id.location_find_btn);
        auth= FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        pdc = new ProgressDialogCustom(this);

        readUserData();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, WriterLocationActivity
                        .class);
                intent.putExtra("LONGITUDE", Double.parseDouble(longTxt.getText().toString()));
                intent.putExtra("LATITUDE", Double.parseDouble(latTxt.getText().toString()));
                intent.putExtra("ADDRESS", addressTxt.getText());
                intent.putExtra("MOBILE_NO", mobileNoTxt.getText().toString());
                startActivity(intent);
            }
        });

        findLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCurrentLocation();
            }
        });

    }

    private void readUserData() {
        pdc.show();
        database.getReference("Users").child(auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().exists()){
                        DataSnapshot snapshot = task.getResult();

                        if(snapshot.child("locLat").getValue() != null){
                            latTxt.setText(snapshot.child("locLat").getValue().toString());
                            longTxt.setText(snapshot.child("locLong").getValue().toString());
                            addressTxt.setText(snapshot.child("address").getValue().toString());
                        }
                        if(snapshot.child("mobileNo").getValue() != null) {
                            mobileNoTxt.setText(snapshot.child("mobileNo").getValue().toString());
                        }

                        pdc.dismiss();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG)
                        .show();
                pdc.dismiss();
            }
        });
    }


    private void saveData() {

        pdc.show();
        if(longTxt.getText() == null || longTxt.getText().toString().equals("") ){
            Toast.makeText(ProfileActivity.this, "Location details are not available",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if(mobileNoTxt.getText() == null || mobileNoTxt.getText().toString().equals("") ){
            Toast.makeText(ProfileActivity.this, "Please enter a Mobile No",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = auth.getCurrentUser().getUid();

        HashMap<String, Object> userData = new HashMap<>();
        userData.put("locLong",Double.parseDouble(longTxt.getText().toString()));
        userData.put("locLat",Double.parseDouble(latTxt.getText().toString()));
        userData.put("address", addressTxt.getText().toString());
        userData.put("mobileNo", mobileNoTxt.getText().toString());

        database.getReference("Users").child(userId).updateChildren(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "Data han been updated!",
                            Toast.LENGTH_LONG).show();
                    pdc.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG)
                        .show();
                pdc.dismiss();
            }
        });
    }

    private void findCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        Task<Location> task = flp.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    currentLocation = location;
                    longTxt.setText(String.format("%s", location.getLongitude()));
                    latTxt.setText(String.format("%s", location.getLatitude()));

                     try {
                         geoCoder = new Geocoder(ProfileActivity.this).getFromLocation(
                                 location.getLatitude(), location.getLongitude(),1).get(0);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }

                     String address = geoCoder.getAddressLine(0) +", "+ geoCoder.getPostalCode();
                     addressTxt.setText(address);
                }
            }
        });
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_CODE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}