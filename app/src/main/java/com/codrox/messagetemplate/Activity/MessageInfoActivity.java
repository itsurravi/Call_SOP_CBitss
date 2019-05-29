package com.codrox.messagetemplate.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import me.gujun.android.taggroup.TagGroup;

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
    TextView status,w_status;
    TextView msg;
    TextView wap;
    TextView remark;
    ImageView s_icon,s2_icon;
    EditText edit_tag;
    Button add_tag;
    TagGroup mTagGroup;

    Button text_btn, wap_btn;
    Button add_Remarks;

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
        s2_icon = findViewById(R.id.s2_color);
        status = findViewById(R.id.status);
        w_status = findViewById(R.id.w_status);
        msg = findViewById(R.id.txt_msg);
        wap = findViewById(R.id.txt_wap);
        text_btn = findViewById(R.id.send_text);
        wap_btn = findViewById(R.id.send_wap);
        remark = findViewById(R.id.txt_remark);
        add_Remarks = findViewById(R.id.add_remark);
        edit_tag = findViewById(R.id.ed_tags);
        add_tag = findViewById(R.id.btn_tag);
        mTagGroup = (TagGroup)findViewById(R.id.txt_tags);

        num.setText(CALL_NUMBER);
        date.setText(setDate(CALL_DATE));
        name.setText(CALL_NAME);

        edit_tag.setSelected(false);

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

        add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = edit_tag.getText().toString().trim();
                saveTag(tag);
                edit_tag.setSelected(false);
                edit_tag.clearFocus();
            }
        });

        new readTag().execute();

        add_Remarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRemarkDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.audio_files, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(MessageInfoActivity.this, AudioRecordingActivity.class);
        i.putExtra("number", CALL_NUMBER);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    private void saveTag(String tag) {
        if(tag.isEmpty())
        {
            edit_tag.setError("Please Fill this Field");
            edit_tag.requestFocus();
            return;
        }

        edit_tag.setText("");
        db.insertTag(CALL_NUMBER, tag);
        new readTag().execute();
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
                    new readData().execute();
                    ad.dismiss();

                    add_tag.requestFocus();
                }
            }
        });

    }

    private String setDate(String call_date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy hh:mm a");
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
                int count = 0;
                do {
                    String REMARKS = cr.getString(cr.getColumnIndex(DataBase.REMARKS));
                    String REMARKS_DATE = cr.getString(cr.getColumnIndex(DataBase.REMARKS_DATE));
                    if (!REMARKS.equals("")) {
//                        sb.append(setDate(REMARKS_DATE) + " >> " + REMARKS);
                        sb.append("<b><font color='black'>" + setDate(REMARKS_DATE) + "<br>>> </b></font>" + REMARKS);
                    }
                    if(count<cr.getCount()-1)
                    {
//                        sb.append("\n");
                        sb.append("<br>");
                    }
                    count++;
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
                s_icon.setImageResource(R.drawable.ic_check);
                status.setText(Constants.Status_Sent);
            }
            else {
                s_icon.setImageResource(R.drawable.ic_block);
                status.setText(Constants.Status_Pending);
            }
            if (!CALL_WAP.equals("")) {
                wap.setText(CALL_WAP);
                s2_icon.setImageResource(R.drawable.ic_check);
                w_status.setText(Constants.Status_Sent);
            }
            else {
                s2_icon.setImageResource(R.drawable.ic_block);
                w_status.setText(Constants.Status_Pending);
            }

            if (!CALL_REMARKS.equals("")) {
//                remark.setText(CALL_REMARKS);
                remark.setText(Html.fromHtml(CALL_REMARKS));
            }
            pd.dismiss();
        }
    }

    class readTag extends AsyncTask<Void, Void, ArrayList<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayList<String> data = new ArrayList<>();
            Cursor c = db.readTag(CALL_NUMBER);
            if(c != null && c.getCount()>0)
            {
                c.moveToFirst();
                do {
                    String d = c.getString(c.getColumnIndex(DataBase.TAG_NAME));
                    data.add(d);
                }
                while (c.moveToNext());
                c.close();
                return data;
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            if(strings!=null)
            {
                mTagGroup.setTags(strings);
            }
        }
    }
}
