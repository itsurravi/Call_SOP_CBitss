package com.codrox.messagetemplate.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.R;

public class AlertActivity extends AppCompatActivity {

    TextView tv;
    Button text, wap;
    String id;
    String num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Intent i = getIntent();
        num = i.getStringExtra(Constants.Number);
        id = i.getStringExtra(Constants.ID);

        tv = findViewById(R.id.number);
        text = findViewById(R.id.send_text);
        wap = findViewById(R.id.send_wap);

        tv.setText(num);

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlertActivity.this, MessageSend.class);
                i.putExtra(Constants.ID, id);
                i.putExtra(Constants.Number, num);
                i.putExtra(Constants.TYPE, Constants.Text);
                startActivity(i);
            }
        });

        wap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlertActivity.this, MessageSend.class);
                i.putExtra(Constants.ID, id);
                i.putExtra(Constants.Number, num);
                i.putExtra(Constants.TYPE, Constants.Whatsapp);
                startActivity(i);
            }
        });
    }
}
