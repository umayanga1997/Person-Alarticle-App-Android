package com.openlab.personalarticle.screen;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openlab.personalarticle.R;
import com.openlab.personalarticle.databinding.ActivityWriterLocationBinding;

public class WriterLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityWriterLocationBinding binding;

    TextView addressTxt;
    TextView mobileNoTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWriterLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addressTxt = findViewById(R.id.address_view_txt);
        mobileNoTxt = findViewById(R.id.contact_no_view_txt);

        if(isLocationPermissionGranted()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        else{
            requestLocationPermission();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Bundle extras = getIntent().getExtras();

        double longi = extras.getDouble("LONGITUDE");
        double lat = extras.getDouble("LATITUDE");

        String address = extras.getString("ADDRESS");
        String mobileNumber = extras.getString("MOBILE_NO");

        if(address != null){
            addressTxt.setText(address);
        }
        if(mobileNumber != null){
            mobileNoTxt.setText(mobileNumber);
        }

        // Add a marker in Sydney and move the camera
        LatLng writerLoc = new LatLng(lat, longi);
        mMap.setMinZoomPreference(15);
        mMap.addMarker(new MarkerOptions().position(writerLoc).title("Writer Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(writerLoc));
    }

    private boolean isLocationPermissionGranted(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return  false;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .ACCESS_FINE_LOCATION},
                101);
    }
}