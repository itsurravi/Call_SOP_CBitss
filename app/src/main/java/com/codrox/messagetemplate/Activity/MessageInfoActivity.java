package com.codrox.messagetemplate.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MessageInfoActivity extends AppCompatActivity {

    String CALL_ID;
    String CALL_NUMBER;
    String CALL_NAME;
    String CALL_DATE;
    String CALL_MSG;
    String CALL_WAP;
    String CALL_STATUS;
    String CALL_REMARKS;


    TextView num, name;
    TextView date;
    ImageView s_icon;
    TextView status;
    TextView msg;
    TextView wap;
    TextView remark;

    Button text_btn, wap_btn;
    FloatingActionButton fab;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_info);

        db = new DataBase(this);

        CALL_ID = "";
        CALL_NUMBER = "";
        CALL_NAME = "";
        CALL_DATE = "";
        CALL_MSG = "";
        CALL_WAP = "";
        CALL_STATUS = "";
        CALL_REMARKS="";

        Bundle bundle = getIntent().getExtras();
        CALL_NUMBER = bundle.getString("CALL_NUMBER");
        CALL_DATE = bundle.getString("CALL_DATE");
        CALL_NAME = bundle.getString("CALL_NAME");

        name = findViewById(R.id.name);
        num = findViewById(R.id.number);
        date = findViewById(R.id.date);
        s_icon = findViewById(R.id.s_color);
        status = findViewById(R.id.status);
        msg = findViewById(R.id.txt_msg);
        wap = findViewById(R.id.txt_wap);
        text_btn = findViewById(R.id.send_text);
        wap_btn = findViewById(R.id.send_wap);
        remark = findViewById(R.id.txt_remark);
        fab = findViewById(R.id.add_remark);

        num.setText(CALL_NUMBER);
        date.setText(setDate(CALL_DATE));
        name.setText(CALL_NAME);

        if (!CALL_NAME.equals("")) {
            name.setVisibility(View.VISIBLE);
        }

        new readData().execute();

        text_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageInfoActivity.this, MessageSend.class);
                i.putExtra(Constants.ID, CALL_ID);
                i.putExtra(Constants.Number, CALL_NUMBER);
                i.putExtra(Constants.TYPE, Constants.Text);
                startActivity(i);
                finish();
            }
        });

        wap_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MessageInfoActivity.this, MessageSend.class);
                i.putExtra(Constants.ID, CALL_ID);
                i.putExtra(Constants.Number, CALL_NUMBER);
                i.putExtra(Constants.TYPE, Constants.Whatsapp);
                startActivity(i);
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemarkDialog();
            }
        });
    }

    private void addRemarkDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.editremarks_layout, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setView(v);
        final AlertDialog ad = ab.create();
        ad.show();

        final EditText ed = v.findViewById(R.id.edit_remarks);
        Button b = v.findViewById(R.id.saveremark);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remark = ed.getText().toString();
                if (remark.isEmpty()) {
                    ed.setError("Please Fill this Field");
                    ed.requestFocus();
                } else {
                    db.insertRemark(CALL_NUMBER, remark, String.valueOf(System.currentTimeMillis()));
                    ad.dismiss();
                }
            }
        });

    }

    private String setDate(String call_date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(call_date));
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    class readData extends AsyncTask<Void, Void, Void> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(MessageInfoActivity.this);
            pd.setCancelable(false);
            pd.setMessage("Reading Data for This Contact.. Please Wait");
            pd.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StringBuilder sb = new StringBuilder();
            sb.append("");
            Cursor cr = db.readRemarks(CALL_NUMBER);
            if (cr != null && cr.getCount() > 0) {
                cr.moveToFirst();
                do {
                    String REMARKS = cr.getString(cr.getColumnIndex(DataBase.REMARKS));
                    String REMARKS_DATE = cr.getString(cr.getColumnIndex(DataBase.REMARKS_DATE));
                    if (!REMARKS.equals("")) {
                        sb.append("\n" + setDate(REMARKS_DATE) + " --> " + REMARKS);
                    }
                }
                while (cr.moveToNext());

                CALL_REMARKS = sb.toString();
            }

            Cursor c = db.readNumberData(CALL_NUMBER);
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String ID = c.getString(c.getColumnIndex(DataBase.CALL_ID));
                    String MSG = c.getString(c.getColumnIndex(DataBase.CALL_MSG));
                    String WAP = c.getString(c.getColumnIndex(DataBase.CALL_WAP));
                    String STATUS = c.getString(c.getColumnIndex(DataBase.CALL_STATUS));

                    CALL_ID = ID;

                    if (!MSG.equals("")) {
                        CALL_MSG = MSG;
                    }
                    if (!WAP.equals("")) {
                        CALL_WAP = WAP;
                    }
                    if (STATUS.equals(Constants.Status_Sent)) {
                        CALL_STATUS = Constants.Status_Sent;
                    }
                }
                while (c.moveToNext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            status.setText(CALL_STATUS);
            if (!CALL_MSG.equals("")) {
                msg.setText(CALL_MSG);
            }
            if (!CALL_WAP.equals("")) {
                wap.setText(CALL_WAP);
            }

            if (!CALL_STATUS.equals(Constants.Status_Sent)) {
                s_icon.setImageResource(R.drawable.ic_block);
                status.setText(Constants.Status_Pending);
            } else {
                s_icon.setImageResource(R.drawable.ic_check);
                status.setText(Constants.Status_Sent);
            }

            if (!CALL_REMARKS.equals("")) {
                remark.setText(CALL_REMARKS);
            }
            pd.dismiss();
        }
    }
}
