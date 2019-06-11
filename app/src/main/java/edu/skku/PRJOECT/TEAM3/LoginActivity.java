package edu.skku.PRJOECT.TEAM3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    public EditText login_id;
    public EditText login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("로그인");
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        login_id = findViewById(R.id.login_editText_id);
        login_password = findViewById(R.id.login_editText_password);

        Button button_login = findViewById(R.id.login_button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn(login_id.getText().toString(), login_password.getText().toString());
            //    Intent intent_login = new Intent(LoginActivity.this, MapActivity.class);
            //    intent_login.putExtra("id", login_id.getText().toString());
            //    intent_login.putExtra("password", login_password.getText().toString());
            //    startActivity(intent_login);
            }
        });

        Button button_register = findViewById(R.id.login_button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_register = new Intent(getApplicationContext(), RegisterActivity.class);
                Log.d(TAG, "register button is clicked");
                startActivity(intent_register);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            //mStatusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    //Change UI according to user data.
    public void updateUI(FirebaseUser account) {
        if (account != null) {
            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Before sending intent in from loginActivity");
            Intent intent_map = new Intent(getApplicationContext(), MapActivity.class);
            startActivity(intent_map);
        } else {
            //Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = login_id.getText().toString();
        if (TextUtils.isEmpty(email)) {
            login_id.setError("Required.");
            valid = false;
        } else {
            login_id.setError(null);
        }

        String password = login_password.getText().toString();
        if (TextUtils.isEmpty(password)) {
            login_password.setError("Required.");
            valid = false;
        } else {
            login_password.setError(null);
        }

        Log.d(TAG, "valid value in validForm:" + valid);
        return valid;
    }


}
