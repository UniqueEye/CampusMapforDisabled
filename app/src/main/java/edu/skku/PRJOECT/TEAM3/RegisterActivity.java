package edu.skku.PRJOECT.TEAM3;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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


public class RegisterActivity extends BaseActivity{

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    private EditText editText_id;
    private EditText editText_email;
    private EditText editText_password1;
    private EditText editText_password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("회원가입");
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        editText_id = findViewById(R.id.register_editText_id);
        editText_email = findViewById(R.id.register_editText_email);
        editText_password1 = findViewById(R.id.register_editText_password1);
        editText_password2 = findViewById(R.id.register_editText_password2);

        Button button_register = findViewById(R.id.register_button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editText_id.getText().toString();
                String email = editText_email.getText().toString();
                String password1 = editText_password1.getText().toString();
                String password2 = editText_password2.getText().toString();

                if (id.isEmpty())
                    alert("아이디를 입력하세요");
                else if (email.isEmpty())
                    alert("이메일을 입력하세요");
                else if (password1.isEmpty())
                    alert("비밀번호를 입력하세요");
                else if (password2.isEmpty())
                    alert("비밀번호 확인을 입력하세요");
                else if (!password1.equals(password2))
                    alert("비밀번호가 일치하지 않습니다");
                else {
                    createAccount(editText_email.getText().toString(), editText_password1.getText().toString());
                    //RegisterActivity.super.onBackPressed();
                }
            }
        });
    }

    //Change UI according to user data.
    public void updateUI(FirebaseUser account) {
        if (account != null) {
            Toast.makeText(this, "U Signed In successfully", Toast.LENGTH_LONG).show();
            Intent intent_map = new Intent(getApplicationContext(), EvaluateActivity.class);
            startActivity(intent_map);
        } else {
            Toast.makeText(this, "U Didnt signed in", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = editText_email.getText().toString();
        if (TextUtils.isEmpty(email)) {
            editText_email.setError("Required.");
            valid = false;
        } else {
            editText_email.setError(null);
        }

        String password = editText_password1.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editText_password1.setError("Required.");
            valid = false;
        } else {
            editText_password1.setError(null);
        }

        Log.d(TAG, "valid value in validForm:" + valid);
        return valid;
    }


/*    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    // [END on_start_check_user]*/
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


    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        Log.d(TAG, "email password after show Progress" +email+" "+password);
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        Log.d(TAG, "before hide Progress" );
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });


    }
}