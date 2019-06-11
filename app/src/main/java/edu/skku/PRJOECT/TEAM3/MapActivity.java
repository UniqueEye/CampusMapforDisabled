package edu.skku.PRJOECT.TEAM3;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

public class MapActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,OnMapReadyCallback{//sungyoun_브랜치

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
        store_mPostReference = FirebaseDatabase.getInstance().getReference();
        building_mPostReference = FirebaseDatabase.getInstance().getReference();

        FragmentManager fragmentManager = getFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap map) {
                    gmap=map;

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        gmap.setMyLocationEnabled(true);
                    } else {
                        // Show rationale and request permission.
                    }
                    LatLng SEOUL = new LatLng(37.293918, 126.975426);
                    gmap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
                    gmap.animateCamera(CameraUpdateFactory.zoomTo(17));
                    //
                    gmap.setLatLngBoundsForCameraTarget(skku_campus);
                    gmap.setMinZoomPreference(13.0f);
                    gmap.setMaxZoomPreference(17.0f);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }


        //get reference to my location icon
        View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).
                getParent()).findViewById(Integer.parseInt("2"));

        // and next place it, for example, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 150, 200);


        building_getFirebaseDatabase();
        store_getFirebaseDatabase();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
                    //Toast.makeText(MapActivity.this, "Building Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
                }
            }
            if(ack == 0){
                //intent store
                Log.d("Name", marker.getTitle());
                Intent store_intent = new Intent(MapActivity.this, StoreActivity.class);
                store_intent.putExtra("name", marker.getTitle());
                startActivity(store_intent);
                //Toast.makeText(MapActivity.this, "Store Marker ID : "+markerId, Toast.LENGTH_SHORT).show();
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

                        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.store_location_pin);
                        Bitmap b = bitmapdraw.getBitmap();
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
        //counter = 0;
        store_mPostReference.child("store").addValueEventListener(postListener);
    }

    final LocationListener networkLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            longitude = location.getLongitude();
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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            gmap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
        }
        LatLng SEOUL = new LatLng(37.293918, 126.975426);
        MarkerOptions markerOptions = new MarkerOptions();

        map.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        map.animateCamera(CameraUpdateFactory.zoomTo(17));


        //gmap.setMyLocationEnabled(true);
      //  gmap.setOnMyLocationButtonClickListener(this);
      //  gmap.setOnMyLocationClickListener(this);


    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
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

    //close the application when back button is clicked in MapActivity.
    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}
