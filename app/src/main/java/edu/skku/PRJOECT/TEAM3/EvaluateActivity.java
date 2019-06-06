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
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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

    private String TAG = "Evaluate";

    public LatLng location;
    public double latitude;
    public double longitude;

    private DatabaseReference mPostReference;

    StorePost post = null;
    String name = "본찌돈까스";

    EditText searchET;
    RatingBar doorRB;
    RatingBar spaceRB;
    RatingBar toiletRB;

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

        mPostReference = FirebaseDatabase.getInstance().getReference();
        getFirebaseDatabase();
        
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                location = place.getLatLng();
                try {
                    latitude = location.latitude;
                    longitude = location.longitude;
                    String text = "Lat: " + latitude + "Long: " + longitude;
                    Toast.makeText(getApplicationContext(), text,
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, "Lat: " + latitude + "Log:" + longitude);
                    Log.i(TAG, "Address: "+place.getAddress());
                }catch(NullPointerException e){
                    Toast.makeText(getApplicationContext(), "Null pointer error try again!",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

      /*  Button button_search = findViewById(R.id.evaluate_button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchET.getText().toString();

                if (query.isEmpty())
                    alert("검색어를 입력하세요");
            }
        });*/
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
                if (!doorRB.isDirty() || !spaceRB.isDirty() || !toiletRB.isDirty()) {
                    alert("평가를 완료해주세요");
                    return super.onOptionsItemSelected(item);
                }

                float door = doorRB.getRating();
                float space = spaceRB.getRating();
                float toilet = toiletRB.getRating();

                if (post == null) {
                    Log.d("post", "null");
                }
                else {
                    post.door = (post.count * post.door + door) / (post.count + 1);
                    post.space = (post.count * post.space + space) / (post.count + 1);
                    post.toilet = (post.count * post.toilet + toilet) / (post.count + 1);
                    post.count++;

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
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key.equals(name)) {
                        Log.d("getDB", "Success");

                        post = postSnapshot.getValue(StorePost.class);
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
        childUpdates.put("/store/" + name, postValues);
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
            Toast.makeText(this, "U Signed In successfully", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MapActivity.class));
        } else {
            Toast.makeText(this, "U Didnt signed in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}
