package edu.skku.PRJOECT.TEAM3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoreActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;

    String name = "알촌";

    TextView nameTV;
    TextView addrTV;
    RatingBar ovrRB;
    RatingBar doorRB;
    RatingBar spaceRB;
    RatingBar toiletRB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("상점 정보");
        setContentView(R.layout.activity_store);

        Intent intent_store = getIntent();

        nameTV = findViewById(R.id.store_textView_name);
        addrTV = findViewById(R.id.store_textView_address);
        ovrRB = findViewById(R.id.store_ratingBar_ovr);
        doorRB = findViewById(R.id.store_ratingBar_door);
        spaceRB = findViewById(R.id.store_ratingBar_space);
        toiletRB = findViewById(R.id.store_ratingBar_toilet);

        mPostReference = FirebaseDatabase.getInstance().getReference();
        getFirebaseDatabase();
    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key.equals(name)) {
                        StorePost get = postSnapshot.getValue(StorePost.class);

                        nameTV.setText(name);

                        String addr = get.addr;
                        float door = get.door;
                        float space = get.space;
                        float toilet = get.toilet;

                        addrTV.setText(addr);
                        ovrRB.setRating((door + space + toilet) / 3);
                        doorRB.setRating(door);
                        spaceRB.setRating(space);
                        toiletRB.setRating(toilet);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        mPostReference.child("store").addValueEventListener(postListener);
    }
}
