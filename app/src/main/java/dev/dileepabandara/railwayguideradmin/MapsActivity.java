/*
   --------------------------------------
      Developed by
      Dileepa Bandara
      https://dileepabandara.github.io
      contact.dileepabandara@gmail.com
      ©dileepabandara.dev
      2020
   --------------------------------------
*/

package dev.dileepabandara.railwayguideradmin;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import dev.dileepabandara.railwayguideradmin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME = 30000;
    private final long MIN_DIST = 10;

    private LatLng latLng;

    TextView gpsNotTurnON, locationDetails;
    TextView locationAddress, locationCity, locationProvince, locationLatitude, locationLongitude;
    ImageView sendSMS, addLocation, deleteLocation, onFocus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);

        Toast.makeText(this, "Please turn on your GPS for this service", Toast.LENGTH_SHORT).show();

        Paper.init(MapsActivity.this);

        LinearLayout viewAllUsers = findViewById(R.id.viewAllUsers);
        viewAllUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), BookedUsers.class));
            }
        });

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

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {

                mMap.clear();

                //Map marker
                int height = 120;
                int width = 120;
                BitmapDrawable bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.icon_marker);
                Bitmap b = bitmapDraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                String makerLatitude = String.valueOf(location.getLatitude());
                String makerLongitude = String.valueOf(location.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Train 4857").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                mMap.setMaxZoomPreference(70.0f);
                mMap.setMinZoomPreference(12.0f);
                marker.showInfoWindow();

                final String latitude = String.valueOf(location.getLatitude());
                final String longitude = String.valueOf(location.getLongitude());

                //Convert location to real address
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                String address;
                String city;
                String province;
                String country;
                String postalCode = "N/A";
                String featureName;

                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    address = addresses.get(0).getAddressLine(0);
                    city = addresses.get(0).getLocality();
                    province = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    featureName = addresses.get(0).getFeatureName();

                    locationAddress = findViewById(R.id.locationAddress);
                    locationCity = findViewById(R.id.locationCity);
                    locationProvince = findViewById(R.id.locationProvince);
                    locationLatitude = findViewById(R.id.locationLatitude);
                    locationLongitude = findViewById(R.id.locationLongitude);

                    locationAddress.setText(address);
                    locationCity.setText(city);
                    locationProvince.setText(province);
                    locationLatitude.setText(latitude);
                    locationLongitude.setText(longitude);

                    Paper.init(MapsActivity.this);
                    Paper.book().write(PrevalentLocation.latitudeKey, latitude);
                    Paper.book().write(PrevalentLocation.longitudeKey, longitude);
                    Paper.book().write(PrevalentLocation.cityKey, city);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //onFocus
                onFocus = findViewById(R.id.onFocus);
                onFocus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MapsActivity.this, "Train 4857", Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));
                    }
                });

                //Send location by sms
                sendSMS = findViewById(R.id.sendSMS);
                sendSMS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(getApplicationContext(), BookedUsers.class));
                        Toast.makeText(MapsActivity.this, "SMS Option", Toast.LENGTH_SHORT).show();

                    }
                });


                //Delete Location
                deleteLocation = findViewById(R.id.deleteLocation);
                deleteLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Delete past location
                        String route = "4857";
                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("trains");
                        reference2.child(route).child("latitude").removeValue();
                        reference2.child(route).child("longitude").removeValue();
                        Toast.makeText(MapsActivity.this, "Location Deleted", Toast.LENGTH_SHORT).show();

                    }
                });

                //Add Location
                addLocation = findViewById(R.id.addLocation);
                addLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Paper.init(MapsActivity.this);
                        final String paperLatitudeKey = Paper.book().read(PrevalentLocation.latitudeKey);
                        final String paperLongitudeKey = Paper.book().read(PrevalentLocation.longitudeKey);

                        final String route = "4857";

                        //Save current location

                        try {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("trains").child(route);
                            reference.child("latitude").setValue(paperLatitudeKey);
                            reference.child("longitude").setValue(paperLongitudeKey);
                            Toast.makeText(MapsActivity.this, "Location Added", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(MapsActivity.this, "Error: " + e, Toast.LENGTH_SHORT).show();
                        }

                    }
                });


                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Toast.makeText(MapsActivity.this, "Train 4857", Toast.LENGTH_SHORT).show();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

                    }
                });

                Toast.makeText(getApplicationContext(), "" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

                Toast.makeText(MapsActivity.this, "Status Changed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onProviderEnabled(String provider) {

                Toast.makeText(MapsActivity.this, "Provider Enabled. Your GPS turned on", Toast.LENGTH_SHORT).show();

                gpsNotTurnON = findViewById(R.id.gpsNotTurnON);
                gpsNotTurnON.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onProviderDisabled(String provider) {

                Toast.makeText(MapsActivity.this, "Provider Disabled. Please turn on your GPS", Toast.LENGTH_SHORT).show();
                gpsNotTurnON = findViewById(R.id.gpsNotTurnON);
                gpsNotTurnON.setVisibility(View.VISIBLE);

            }
        };

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "" + e, Toast.LENGTH_LONG).show();
        }

    }

}
