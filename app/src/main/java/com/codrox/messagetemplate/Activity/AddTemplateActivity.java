package com.codrox.messagetemplate.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.R;

public class AddTemplateActivity extends AppCompatActivity {

    Spinner sp;
    EditText ed, tit;
    Button b;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);

        db = new DataBase(this);

        sp = (Spinner)findViewById(R.id.spinner);
        ed = (EditText) findViewById(R.id.editText);
        tit = (EditText) findViewById(R.id.title);
        b = (Button)findViewById(R.id.save);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = sp.getSelectedItemPosition();
                String msg = ed.getText().toString();
                String title = tit.getText().toString();
                String msgtype = sp.getSelectedItem().toString();

                if(pos==0)
                {
                    Toast t = Toast.makeText(AddTemplateActivity.this, "Please Select Message Type", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0,0);
                    t.show();
                    return;
                }

                if(msg.isEmpty())
                {
                    ed.setError("Please Enter Message Here");
                    ed.requestFocus();
                    Toast t = Toast.makeText(AddTemplateActivity.this, "Please Enter Message", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0,0);
                    t.show();
                    return;
                }
                if(title.isEmpty())
                {
                    tit.setError("Please Enter Title Here");
                    tit.requestFocus();
                    Toast t = Toast.makeText(AddTemplateActivity.this, "Please Enter Title", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0,0);
                    t.show();
                    return;
                }
                if(msgtype.equals("Text Message"))
                {
                    db.insertTemplate(msg, title, Constants.Text);
                }
                else if(msgtype.equals("WhatsApp Message"))
                {
                    db.insertTemplate(msg, title, Constants.Whatsapp);
                }
                Toast t = Toast.makeText(AddTemplateActivity.this, "Template Saved", Toast.LENGTH_LONG);
                t.setGravity(Gravity.CENTER, 0,0);
                t.show();
                sp.setSelection(0);
                ed.setText("");
                tit.setText("");
            }
        });
    }
}
