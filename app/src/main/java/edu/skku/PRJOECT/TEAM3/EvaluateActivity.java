package edu.skku.PRJOECT.TEAM3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


public class EvaluateActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String apiKey = "AIzaSyDOn85JQH3cxvUsfgmc5YOJT3VqTs8suqs";
    int door_ack =0, space_ack = 0, toilet_ack = 0;
    private String TAG = "Evaluate";

    public LatLng location;
    public double latitude;
    public double longitude;
    public float door, space, toilet;
    private DatabaseReference mPostReference;
    StorePost post = new StorePost();
    String place_name;
    TextView textView_name, textView_addr;
    String addr;
    RatingBar doorRB, spaceRB, toiletRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("접근성 평가");
        setContentView(R.layout.activity_evaluate);

        // Initialize Places.
        Places.initialize(getApplicationContext(), apiKey);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(getApplicationContext());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Intent intent_evaluate = getIntent();

        //searchET = findViewById(R.id.evaluate_editText_search);
        doorRB = findViewById(R.id.evaluate_ratingBar_door);
        spaceRB = findViewById(R.id.evaluate_ratingBar_space);
        toiletRB = findViewById(R.id.evaluate_ratingBar_toilet);
        textView_name = findViewById(R.id.evaluate_textView_name);
        textView_name.setSelected(true);
        textView_addr = findViewById(R.id.evaluate_textView_address);
        textView_addr.setSelected(true);

        mPostReference = FirebaseDatabase.getInstance().getReference();

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG, Place.Field.ADDRESS));
        autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(
                new LatLng(37.282266, 126.955033), new LatLng(37.305273, 126.990678)));


        ///rating listener
        doorRB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                door_ack++;
            }
        });

        spaceRB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                space_ack++;
            }
        });
        toiletRB.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                toilet_ack++;
            }
        });
        Log.d("\n1. ack ", Integer.toString(door_ack)+" "+Integer.toString(space_ack)+" "+Integer.toString(toilet_ack) );
        ///


        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                place_name = place.getName();
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId()+","+place_name);
                textView_name.setText(place_name);

                location = place.getLatLng();
                try {
                    latitude = location.latitude;
                    longitude = location.longitude;
                    addr = place.getAddress();
                    textView_addr.setText(addr);
                    String text = "Lat: " + latitude + "Long: " + longitude;
                    //Toast.makeText(getApplicationContext(), text,
                           // Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Lat: " + latitude + "Log:" + longitude);
                    Log.i(TAG, "Address: "+addr);
                }catch(NullPointerException e){

                }

                getFirebaseDatabase();//이미 식당이 있으면 firebase로 부터 가져오고, 그렇지 않으면 가져오지 않음(post에 값을 추가하지 않음)

                if(post.count == 0){//신규 등록 상점
                    post.name = place_name;
                    post.addr = addr;
                    post.lat = latitude;
                    post.lon = longitude;
                    Log.i(TAG, "Name " + place_name + " addr: " + addr + " Lat: " + latitude + " Lon: "+latitude);
                }
                else{
                    Log.i(TAG, "기존 식당 Name " + place_name + " addr: " + addr + " Lat: " + latitude + " Lon: "+latitude);
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        if (savedInstanceState != null) {
            place_name = savedInstanceState.getString("name");
            addr = savedInstanceState.getString("addr");
            textView_name.setText(place_name);
            textView_addr.setText(addr);

            post.name = savedInstanceState.getString("post_name");
            post.addr = savedInstanceState.getString("post_addr");
            post.lat = savedInstanceState.getDouble("post_lat");
            post.lon = savedInstanceState.getDouble("post_lon");
        }
    } //onCreate

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        place_name = textView_name.getText().toString();
        addr = textView_addr.getText().toString();
        outState.putString("name", place_name);
        outState.putString("addr", addr);
        if (true) {
            outState.putString("post_name", post.name);
            outState.putString("post_addr", post.addr);
            outState.putDouble("post_lat", post.lat);
            outState.putDouble("post_lon", post.lon);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.evaluate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.evaluate_action_ok:

                if(post.name==null){
                    alert("상점을 검색해주세요");
                    return super.onOptionsItemSelected(item);
                }
                else if(door_ack*space_ack*toilet_ack == 0) {
                    alert("평가를 완료해주세요");
                    return super.onOptionsItemSelected(item);
                }


                door_ack = 0;
                space_ack = 0;
                toilet_ack = 0;
                //initialize the ack
                Log.d("\n2. ack ", Integer.toString(door_ack)+" "+Integer.toString(space_ack)+" "+Integer.toString(toilet_ack) );

                door = doorRB.getRating();
                space = spaceRB.getRating();
                toilet = toiletRB.getRating();
                if (post == null) {
                    Log.d("post", "null");
                }
                else {
                    post.door = (post.count * post.door + door) / (post.count+1);
                    post.space = (post.count * post.space + space) / (post.count+1);
                    post.toilet = (post.count * post.toilet + toilet) / (post.count+1);
                    post.count++;
                    //이 시점에 post에 모든 정보가 추가되어야 함.
                    postFirebaseDatabase();
                }

                super.onBackPressed();
                return super.onOptionsItemSelected(item);
            case R.id.sign_out:
                signOut();
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("getFirebase: ", "Check this out");

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    Log.d("Key-> ", key+"/"+place_name);

                    if (key.equals(place_name)) {//식당 이름이랑 주소가 같은 것만 가져온다.
                        Log.d("getDB", "Success");
                        post = postSnapshot.getValue(StorePost.class);
                        Log.d(TAG, "firebase Name " + post.name + " addr: " + post.addr + " Lat: " + post.lat + " Lon: "+post.lon+ " Door: "+ post.door);
                        Log.d(TAG,"post.count: "+String.valueOf(post.count));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        mPostReference.child("store").addValueEventListener(postListener);
    }

    public void postFirebaseDatabase() {
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        //post = new StorePost(name, addr, latitude, longitude, count, door, space, toilet, count);//?

        childUpdates.put("/store/" + place_name, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    void alert(String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    //Change UI according to user data.
    public void updateUI(FirebaseUser account) {
        if (account != null) {
            //Toast.makeText(this, "U Signed In successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MapActivity.class));
        } else {
            // Toast.makeText(this, "U Didnt signed in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}
