package edu.skku.PRJOECT.TEAM3;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setSubtitle("회원가입");
        setContentView(R.layout.activity_register);

        final EditText editText_id = findViewById(R.id.register_editText_id);
        final EditText editText_email = findViewById(R.id.register_editText_email);
        final EditText editText_password1 = findViewById(R.id.register_editText_password1);
        final EditText editText_password2 = findViewById(R.id.register_editText_password2);

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
                else
                    RegisterActivity.super.onBackPressed();
            }
        });
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
}
