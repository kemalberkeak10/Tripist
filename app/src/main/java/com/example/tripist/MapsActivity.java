package com.example.tripist;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;



    LocationManager locationManager;
    LocationListener locationListener;
    SQLiteDatabase database;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToMain = new Intent(this,MainActivity.class);
        startActivity(intentToMain);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.matches("new")){
            //KULLANICIDAN KONUM İZNİ
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationListener= new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    SharedPreferences sharedPreferences = MapsActivity.this.getSharedPreferences("com.example.tripist", MODE_PRIVATE);
                    boolean trackBoolean = sharedPreferences.getBoolean("trackBoolean", false);

                    if (trackBoolean == false) {
                        LatLng kKonum = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kKonum, 15));
                        sharedPreferences.edit().putBoolean("trackBoolean",true).apply();
                    }

                }
            };

            //kulanıcı izni kontrol etmek
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},100);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                Location sonKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(sonKonum != null){
                    LatLng ksonKonum = new LatLng(sonKonum.getLatitude(),sonKonum.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ksonKonum,15));
                }
            }

        } else {
            //once  KAYDEDİLENEN DATALAR SQLİTE intent data

           Yerler yer = (Yerler) intent.getSerializableExtra("yer");
           LatLng latLng = new LatLng(yer.latitude,yer.longitude);
           String yerName = yer.name;
           mMap.addMarker(new MarkerOptions().position(latLng).title(yerName));
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }

        /*  // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

       */
    }
    // izne göre kontrol yapmak

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length> 0){
            if(requestCode==100){
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

                    Intent intent = getIntent();
                    String info = intent.getStringExtra("info");

                    if ( info.matches("new")){
                        Location sonKonum = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if(sonKonum != null){
                            LatLng ksonKonum = new LatLng(sonKonum.getLatitude(),sonKonum.getLongitude());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ksonKonum,15));
                        }else{
                            //sqlite data
                           // mMap.clear();
                            Yerler yer = (Yerler) intent.getSerializableExtra("yerler");
                            LatLng latLng = new LatLng(yer.latitude,yer.longitude);
                            String yerName = yer.name;
                            mMap.addMarker(new MarkerOptions().position(latLng).title(yerName));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        }

                    }
                }
            }
        }
    }
          // uzun basıldığında adres ekleme
    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if ( addressList != null && addressList.size() > 0){
                if(addressList.get(0).getThoroughfare() != null){
                    address += addressList.get(0).getThoroughfare();

                    if(addressList.get(0).getSubThoroughfare() != null){
                        address += "";
                        address += addressList.get(0).getSubThoroughfare();
                    }
                }
            }else{
                // adres alamazsa default
                address = "new place" ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

       // mMap.clear();

        mMap.addMarker(new MarkerOptions().title(address).position(latLng));

        Double latitude = latLng.latitude;
        Double longitude = latLng.longitude;

        final Yerler yer = new Yerler(address,latitude,longitude);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Burayı Kaydetmek Ister Misin");
        alertDialog.setMessage(yer.name);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DATABASE ACMAK YADA OLUSTURMAK
                try {
                    database = MapsActivity.this.openOrCreateDatabase("Yerler", MODE_PRIVATE, null);

                   database.execSQL("CREATE TABLE IF NOT EXISTS yerler(id INTEGER PRIMARY KEY,name VARCHAR ,latitude VARCHAR , longitude VARCHAR)");

                    String toCompile = "INSERT INTO yerler (name,latitude,longitude) VALUES (?,?,?)";

                    SQLiteStatement sqLiteStatement = database.compileStatement(toCompile);
                    sqLiteStatement.bindString(1,yer.name);
                    sqLiteStatement.bindString(2,String.valueOf(yer.latitude));
                    sqLiteStatement.bindString(3,String.valueOf(yer.longitude));
                    sqLiteStatement.execute();

                    Toast.makeText(getApplicationContext(),"SAVED",Toast.LENGTH_LONG).show();


                }catch(Exception e){
                    e.printStackTrace();

                }
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(),"Kapattıldı",Toast.LENGTH_LONG);

            }
        });
    alertDialog.show();


    }
}

