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


public class Evaluate_intent_Activity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String apiKey = "AIzaSyDOn85JQH3cxvUsfgmc5YOJT3VqTs8suqs";
    int door_ack =0, space_ack = 0, toilet_ack = 0;
    private String TAG = "Evaluate_intent";

    public LatLng location;
    public String name, addr;
    public double latitude;
    public double longitude;
    public float door, space, toilet;
    public int  count;

    private DatabaseReference mPostReference;
    StorePost post = new StorePost();

    TextView textView_name, textView_addr;
    RatingBar doorRB, spaceRB, toiletRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("접근성 평가");
        setContentView(R.layout.activity_evaluate_intent);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Intent intent_evaluate = getIntent();
        intent_evaluate = getIntent();
        post.name = intent_evaluate.getExtras().getString("name");
        post.addr = intent_evaluate.getExtras().getString("addr");
        post.lat = intent_evaluate.getExtras().getDouble("lat");
        post.lon = intent_evaluate.getExtras().getDouble("lon");
        post.door = intent_evaluate.getExtras().getFloat("door");
        post.toilet = intent_evaluate.getExtras().getFloat("toilet");
        post.space = intent_evaluate.getExtras().getFloat("space");
        post.count = intent_evaluate.getExtras().getInt("count");

        doorRB = findViewById(R.id.evaluate_ratingBar_door);
        spaceRB = findViewById(R.id.evaluate_ratingBar_space);
        toiletRB = findViewById(R.id.evaluate_ratingBar_toilet);
        textView_name = findViewById(R.id.evaluate_textView_name);
        textView_name.setSelected(true);
        textView_addr = findViewById(R.id.evaluate_textView_address);
        textView_addr.setSelected(true);
        textView_name.setText(post.name);
        textView_addr.setText(post.addr);
        mPostReference = FirebaseDatabase.getInstance().getReference();

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

    } //onCreate

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

    public void postFirebaseDatabase() {
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/store/" + post.name, postValues);
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
            //Toast.makeText(this, "U Didnt signed in", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }
}

