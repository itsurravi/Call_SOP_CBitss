package com.codrox.messagetemplate.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codrox.messagetemplate.Prefrence;
import com.codrox.messagetemplate.R;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button btn;

    Prefrence pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = new Prefrence(this);
        if(pref.loggedIn())
        {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        btn = (Button)findViewById(R.id.login);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usrname = username.getText().toString();
                String pswd = password.getText().toString();

                loginUser(usrname, pswd);
            }
        });
    }

    private void loginUser(String usrname, String pswd) {
        if(usrname.isEmpty())
        {
            username.setError("Field Cannot Be Empty");
            username.requestFocus();
            return;
        }
        if(pswd.isEmpty())
        {
            password.setError("Field Cannot Be Empty");
            password.requestFocus();
            return;
        }

        if(usrname.equals("username") && pswd.equals("qwerty"))
        {
            pref.setData(usrname, pswd);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else
        {
            Toast.makeText(this, "Wrong Username or Password", Toast.LENGTH_SHORT).show();
        }
    }
}
