package edu.skku.PRJOECT.TEAM3;

import android.content.Intent;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BuildingActivity extends AppCompatActivity {

    private DatabaseReference mPostReference;

    String name = "제2공학관";
    int low;
    int high;
    String sort = "name";

    TextView textView_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("건물 정보");
        setContentView(R.layout.activity_building);

        Intent intent_building = getIntent();

        mPostReference = FirebaseDatabase.getInstance().getReference();
        getFirebaseDatabase();
    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "Data is updated");

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key.equals(name)) {
                        BuildingPost get = postSnapshot.getValue(BuildingPost.class);

                        low = get.low;
                        high = get.high;

                        Log.d("onDataChange", key + Integer.toString(low) + Integer.toString(high));

                        textView_name = findViewById(R.id.building_textView_name);
                        textView_name.setText(name);

                        TableRow row_B1 = findViewById(R.id.building_row_B1);
                        TableRow row_1F = findViewById(R.id.building_row_1F);
                        TableRow row_2F = findViewById(R.id.building_row_2F);
                        TableRow row_3F = findViewById(R.id.building_row_3F);
                        TableRow row_4F = findViewById(R.id.building_row_4F);
                        TableRow row_5F = findViewById(R.id.building_row_5F);
                        TableRow row_6F = findViewById(R.id.building_row_6F);

                        Log.d("Low and High", Integer.toString(low) + Integer.toString(high));

                        for (int i = low; i <= high; i++) {
                            switch (i) {
                                case 0:
                                    row_B1.setVisibility(View.VISIBLE);
                                    break;
                                case 1:
                                    row_1F.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    row_2F.setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    row_3F.setVisibility(View.VISIBLE);
                                    break;
                                case 4:
                                    row_4F.setVisibility(View.VISIBLE);
                                    break;
                                case 5:
                                    row_5F.setVisibility(View.VISIBLE);
                                    break;
                                case 6:
                                    row_6F.setVisibility(View.VISIBLE);
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mPostReference.child("building").addValueEventListener(postListener);
    }
}