package edu.skku.PRJOECT.TEAM3;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("로그인");
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        final EditText login_id = findViewById(R.id.login_editText_id);
        final EditText login_password = findViewById(R.id.login_editText_password);

        Button button_login = findViewById(R.id.login_button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_login = new Intent(LoginActivity.this, MapActivity.class);
                intent_login.putExtra("id", login_id.getText().toString());
                intent_login.putExtra("password", login_password.getText().toString());
                startActivity(intent_login);
            }
        });

        Button button_register = findViewById(R.id.login_button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_register = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent_register);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
     //   updateUI(currentUser);
    }


}
