package edu.skku.PRJOECT.TEAM3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StoreActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;

    String name;
    StorePost get = new StorePost();
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

        Intent intent = getIntent();
        name = intent.getExtras().getString("name");

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
                        get = postSnapshot.getValue(StorePost.class);

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.evaluate_intent, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//접근성 평가로 넘어감
        switch (item.getItemId()) {
            case R.id.evaluate_action_add:
                Intent intent_evaluate = new Intent(getApplicationContext(), Evaluate_intent_Activity.class);
                intent_evaluate.putExtra("name", get.name);
                intent_evaluate.putExtra("addr",get.addr);
                intent_evaluate.putExtra("lat",get.lat);
                intent_evaluate.putExtra("lon",get.lon );
                intent_evaluate.putExtra("door",get.door);
                intent_evaluate.putExtra("space",get.space);
                intent_evaluate.putExtra("toilet",get.toilet);
                intent_evaluate.putExtra("count",get.count);
                startActivity(intent_evaluate);
                return super.onOptionsItemSelected(item);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
