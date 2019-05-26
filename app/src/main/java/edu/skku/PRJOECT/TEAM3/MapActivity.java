package edu.skku.PRJOECT.TEAM3;


import android.content.Intent;

import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gmap;
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("캠퍼스 맵");
        setContentView(R.layout.activity_map);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent_login = getIntent();
        FloatingActionButton button= findViewById(R.id.floatingActionButton);
        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( getApplicationContext(),
                                android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( MapActivity.this, new String[]
                            { android.Manifest.permission.ACCESS_FINE_LOCATION },0 );
                }
                else{
                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,0, gpsLocationListener);
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,100, 0, networkLocationListener);
                }

            }
        });
    }

    final LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
            LatLng my_loc = new LatLng(latitude, longitude);
            gmap.clear();

            Marker new_mkr = gmap.addMarker(new MarkerOptions()
                    .position(my_loc)
                    .title("Here")
                    .snippet("I got you"));
            gmap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
            gmap.animateCamera(CameraUpdateFactory.zoomTo(17));

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
    };
    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            double altitude = location.getAltitude();
            LatLng my_loc = new LatLng(latitude, longitude);
            return;
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };
    @Override
    public void onMapReady(final GoogleMap map) {
        this.gmap=map;
        LatLng SEOUL = new LatLng(37.293918, 126.975426);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("SKKU");
        markerOptions.snippet("Welcome to SKKU");
        map.addMarker(markerOptions);
        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(17));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//상단 map 메뉴를 띄운다
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//접근성 평가로 넘어감
        switch (item.getItemId()) {
            case R.id.map_action_add:
                Intent intent_evaluate = new Intent(MapActivity.this, EvaluateActivity.class);
                startActivity(intent_evaluate);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
