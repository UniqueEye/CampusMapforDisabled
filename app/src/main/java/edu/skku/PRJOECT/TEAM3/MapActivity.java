package edu.skku.PRJOECT.TEAM3;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("캠퍼스 맵");
        setContentView(R.layout.activity_map);

        Intent intent_login = getIntent();

        Button button_building = findViewById(R.id.map_button_building);
        button_building.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_building = new Intent(MapActivity.this, BuildingActivity.class);
                startActivity(intent_building);
            }
        });

        Button button_store = findViewById(R.id.map_button_store);
        button_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_store = new Intent(MapActivity.this, StoreActivity.class);
                startActivity(intent_store);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_action_add:
                Intent intent_evaluate = new Intent(MapActivity.this, EvaluateActivity.class);
                startActivity(intent_evaluate);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
