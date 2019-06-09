package edu.skku.PRJOECT.TEAM3;


import android.content.Intent;

import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{//sungyoun_브랜치

    private int counter;
    private FirebaseAuth mAuth;
    private static final String TAG = "MapActivity";
    private DatabaseReference store_mPostReference, building_mPostReference;
    public GoogleMap gmap;
    public double longitude;
    public double latitude;
    public double altitude;
    StorePost store_post = new StorePost();
    BuildingPost building_post = new BuildingPost();
    ArrayList<String> building_id = new ArrayList<>();
    ArrayList<String> store_id = new ArrayList<>();
    final LatLngBounds skku_campus = new LatLngBounds(
            new LatLng(37.282266, 126.955033), new LatLng(37.305273, 126.990678));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("캠퍼스 맵");
        setContentView(R.layout.activity_map);
        mAuth = FirebaseAuth.getInstance();


        Button btn_bldg = findViewById(R.id.btn_bldg);      //building으로 넘어가기 위한 임시 버튼
        btn_bldg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_building = new Intent(MapActivity.this, BuildingActivity.class);
                startActivity(intent_building);
            }
        });

        Button btn_store = findViewById(R.id.btn_store);    //store로 넘어가기 위한 임시 버튼
        btn_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_store = new Intent(MapActivity.this, StoreActivity.class);
                startActivity(intent_store);
            }
        });
        store_mPostReference = FirebaseDatabase.getInstance().getReference();
        building_mPostReference = FirebaseDatabase.getInstance().getReference();

        FragmentManager fragmentManager = getFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap map) {
                    gmap=map;
                    LatLng SEOUL = new LatLng(37.293918, 126.975426);

                    /*MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(SEOUL);
                    markerOptions.title("SKKU");
                    markerOptions.snippet("Welcome to SKKU");
                    map.addMarker(markerOptions);
                    */
                    map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                    map.animateCamera(CameraUpdateFactory.zoomTo(17));
                    //
                    gmap.setLatLngBoundsForCameraTarget(skku_campus);
                    //
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }



        FloatingActionButton button= findViewById(R.id.floatingActionButton);
        final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d("Current location", "Here");
                LatLng my_loc = new LatLng(latitude, longitude);

                Marker new_mkr = gmap.addMarker(new MarkerOptions()
                        .position(my_loc)
                        .title("Here")
                        .snippet("I got you"));
                gmap.moveCamera(CameraUpdateFactory.newLatLng(my_loc));
                gmap.animateCamera(CameraUpdateFactory.zoomTo(17));

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
        building_getFirebaseDatabase();
        store_getFirebaseDatabase();
    }

    public void building_getFirebaseDatabase() {//Firebase에서 location을 받아 pin을 찍는다.
        //counter = 0;

        final ValueEventListener postListener = new ValueEventListener() {
            final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String key = postSnapshot.getKey();
                        Log.d("Building Key->", key);
                        building_post = postSnapshot.getValue(BuildingPost.class);
                        double lat = building_post.lat;
                        double lon = building_post.lon;

                        LatLng my_loc = new LatLng(lat, lon);
                        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.building_location_pin);
                        Bitmap b=bitmapdraw.getBitmap();
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                        Marker new_mkr = gmap.addMarker(new MarkerOptions()
                                .position(my_loc)
                                .title(key)
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                        building_id.add(new_mkr.getId());
                        gmap.setOnInfoWindowClickListener(infoWindowClickListener);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
           building_mPostReference.child("building").addValueEventListener(postListener);
    }

    GoogleMap.OnInfoWindowClickListener infoWindowClickListener = new GoogleMap.OnInfoWindowClickListener() {
        @Override
        public void onInfoWindowClick(Marker marker) {
            String markerId = marker.getId();
            int ack = 0;
            Iterator building_it = building_id.iterator();
            //Iterator store_it = store_id.iterator();

            while(building_it.hasNext()){
                if(building_it.next().equals(markerId)) {
                    ack = 1;
                    Log.d("Name", marker.getTitle());
                    //building intent
                    Intent building_intent = new Intent(MapActivity.this, BuildingActivity.class);
                    building_intent.putExtra("name", marker.getTitle());
                    startActivity(building_intent);
                    Toast.makeText(MapActivity.this, "Building Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
                }
            }
            if(ack == 0){
                //intent store
                Log.d("Name", marker.getTitle());
                Intent store_intent = new Intent(MapActivity.this, StoreActivity.class);
                store_intent.putExtra("name", marker.getTitle());
                startActivity(store_intent);
                Toast.makeText(MapActivity.this, "Store Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
            }
        }
    };



    public void store_getFirebaseDatabase() {//Firebase에서 location을 받아 pin을 찍는다.
        //counter = 0;

        final ValueEventListener postListener = new ValueEventListener() {
            final LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String key = postSnapshot.getKey();
                    Log.d("Store Key->", key);


                        store_post = postSnapshot.getValue(StorePost.class);
                        String addr = store_post.addr;
                        double lat = store_post.lat;
                        double lon = store_post.lon;

                        LatLng my_loc = new LatLng(lat, lon);

                    BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.store_location_pin);
                    Bitmap b=bitmapdraw.getBitmap();
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 150, 150, false);
                    Marker new_mkr = gmap.addMarker(new MarkerOptions()
                                .position(my_loc)
                                .title(key)
                                .snippet(addr)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    store_id.add(new_mkr.getId());
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        store_mPostReference.child("store").addValueEventListener(postListener);
    }

    final LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            altitude = location.getAltitude();

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
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            altitude = location.getAltitude();
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };
    @Override
    public void onMapReady(final GoogleMap map) {
        gmap=map;
        LatLng SEOUL = new LatLng(37.293918, 126.975426);
        MarkerOptions markerOptions = new MarkerOptions();
        /*
        markerOptions.position(SEOUL);
        markerOptions.title("SKKU");
        markerOptions.snippet("Welcome to SKKU");
        map.addMarker(markerOptions);
        */
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
                Intent intent_evaluate = new Intent(getApplicationContext(), EvaluateActivity.class);
                startActivity(intent_evaluate);
                return super.onOptionsItemSelected(item);
            case R.id.sign_out:
                signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    //Change UI according to user data.
    public void updateUI(FirebaseUser account) {
        if (account != null) {
            Toast.makeText(this, "U Signed In successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MapActivity.class));
        } else {
            Toast.makeText(this, "U Didnt signed in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

}
