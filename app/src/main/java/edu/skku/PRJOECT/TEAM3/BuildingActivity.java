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
import android.widget.ImageView;
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

    public void setFloorVisible(int low, int high, int[] val) {
        for (int i = low; i <= high; i++) {
            switch (i) {
                case 0:
                    findViewById(R.id.building_row_B1).setVisibility(View.VISIBLE);
                    setIconVisible(
                            (ImageView)findViewById(R.id.building_B1_ramp),
                            (ImageView)findViewById(R.id.building_B1_elevator),
                            (ImageView)findViewById(R.id.building_B1_toilet),
                            val[0]);
                    break;
                case 1:
                    findViewById(R.id.building_row_1F).setVisibility(View.VISIBLE);
                    setIconVisible(
                            (ImageView)findViewById(R.id.building_1F_ramp),
                            (ImageView)findViewById(R.id.building_1F_elevator),
                            (ImageView)findViewById(R.id.building_1F_toilet),
                            val[1]);
                    break;
                case 2:
                    findViewById(R.id.building_row_2F).setVisibility(View.VISIBLE);
                    setIconVisible(
                            (ImageView)findViewById(R.id.building_2F_ramp),
                            (ImageView)findViewById(R.id.building_2F_elevator),
                            (ImageView)findViewById(R.id.building_2F_toilet),
                            val[2]);
                    break;
                case 3:
                    findViewById(R.id.building_row_3F).setVisibility(View.VISIBLE);
                    setIconVisible(
                            (ImageView)findViewById(R.id.building_3F_ramp),
                            (ImageView)findViewById(R.id.building_3F_elevator),
                            (ImageView)findViewById(R.id.building_3F_toilet),
                            val[3]);
                    break;
                case 4:
                    findViewById(R.id.building_row_4F).setVisibility(View.VISIBLE);
                    setIconVisible(
                            (ImageView)findViewById(R.id.building_4F_ramp),
                            (ImageView)findViewById(R.id.building_4F_elevator),
                            (ImageView)findViewById(R.id.building_4F_toilet),
                            val[4]);
                    break;
                case 5:
                    findViewById(R.id.building_row_5F).setVisibility(View.VISIBLE);
                    setIconVisible(
                            (ImageView)findViewById(R.id.building_5F_ramp),
                            (ImageView)findViewById(R.id.building_5F_elevator),
                            (ImageView)findViewById(R.id.building_5F_toilet),
                            val[5]);
                    break;
                case 6:
                    findViewById(R.id.building_row_6F).setVisibility(View.VISIBLE);
                    setIconVisible(
                            (ImageView)findViewById(R.id.building_6F_ramp),
                            (ImageView)findViewById(R.id.building_6F_elevator),
                            (ImageView)findViewById(R.id.building_6F_toilet),
                            val[6]);
                    break;
            }
        }
    }

    public void setIconVisible(ImageView ramp, ImageView ev, ImageView toilet, int val) {
        if ((val & 4) == 4)
            ramp.setVisibility(View.VISIBLE);
        else
            ramp.setVisibility(View.INVISIBLE);

        if ((val & 2) == 2)
            ev.setVisibility(View.VISIBLE);
        else
            ev.setVisibility(View.INVISIBLE);

        if ((val & 1) == 1)
            toilet.setVisibility(View.VISIBLE);
        else
            toilet.setVisibility(View.INVISIBLE);
    }

    public void getFirebaseDatabase() {
        final ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    if (key.equals(name)) {
                        BuildingPost get = postSnapshot.getValue(BuildingPost.class);

                        int low = get.low;
                        int high = get.high;

                        int[] val = new int[high + 1];
                        for (int i = low; i <= high; i++) {
                            switch (i) {
                                case 0:
                                    val[0] = get.b1;
                                    break;
                                case 1:
                                    val[1] = get.f1;
                                    break;
                                case 2:
                                    val[2] = get.f2;
                                    break;
                                case 3:
                                    val[3] = get.f3;
                                    break;
                                case 4:
                                    val[4] = get.f4;
                                    break;
                                case 5:
                                    val[5] = get.f5;
                                    break;
                            }
                        }

                        textView_name = findViewById(R.id.building_textView_name);
                        textView_name.setText(name);

                        setFloorVisible(low, high, val);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };

        mPostReference.child("building").addValueEventListener(postListener);
    }
}