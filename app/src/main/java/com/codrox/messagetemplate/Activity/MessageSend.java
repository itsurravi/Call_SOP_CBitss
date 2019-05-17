package com.codrox.messagetemplate.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codrox.messagetemplate.Adapter.Temp_List_Adapter;
import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.Modals.Model_Temp;
import com.codrox.messagetemplate.R;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MessageSend extends AppCompatActivity {

    int pos = -1;

    ListView lv;
    Button send;
    EditText name;

    String id, type, number;
    DataBase db;

    List<Model_Temp> data;
    Temp_List_Adapter ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_send);

        lv = (ListView) findViewById(R.id.list);
        send = (Button) findViewById(R.id.send);
        name = (EditText) findViewById(R.id.nameHere);

        db = new DataBase(this);
        data = new ArrayList<>();

        Bundle bundle = getIntent().getExtras();

        id = bundle.getString(Constants.ID);
        type = bundle.getString(Constants.TYPE);
        number = bundle.getString(Constants.Number);

        new DataFetch().execute();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < data.size(); i++) {
                    data.get(i).setIsSelected(false);
                }

                data.get(position).setIsSelected(true);
                ad.notifyDataSetChanged();

                pos = position;
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String n = name.getText().toString().trim();
                if (n.isEmpty()) {
                    Toast.makeText(MessageSend.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pos == -1) {
                    Toast.makeText(MessageSend.this, "Select One Template To Send", Toast.LENGTH_SHORT).show();
                } else {
                    String msg = "Dear " + n + ",\n\n" + data.get(pos).getTEMP_MSG();
                    switch (type) {
                        case Constants.Text:
                            //send textMessage

                            sendMessage(number, msg);

                            db.updateCallText(id, n, msg, Constants.Status_Sent);

                            break;
                        case Constants.Whatsapp:
                            //send Whatsapp
                            PackageManager packageManager = getPackageManager();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            try {
                                String url = null;
                                if (number.startsWith("+91")) {
                                    url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(msg, "UTF-8");
                                } else if (number.startsWith("0")) {
                                    number = number.replaceFirst("0", "+91");
                                    url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(msg, "UTF-8");
                                } else {
                                    url = "https://api.whatsapp.com/send?phone=+91" + number + "&text=" + URLEncoder.encode(msg, "UTF-8");
                                }
                                i.setPackage("com.whatsapp");
                                i.setData(Uri.parse(url));
                                if (i.resolveActivity(packageManager) != null) {
                                    startActivity(i);
                                    db.updateCallWap(id, n, msg, Constants.Status_Sent);
                                }
                            } catch (Exception e) {
                                Toast.makeText(MessageSend.this, "Whatsapp not Installed", Toast.LENGTH_LONG).show();
                            }

                            break;
                    }
                    finish();
                }
            }
        });

    }

    class DataFetch extends AsyncTask<Void, Void, List<Model_Temp>> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MessageSend.this);
            pd.setMessage("Wait");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected List<Model_Temp> doInBackground(Void... voids) {
            Cursor c = db.readAllTemp();
            data.clear();
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {

                    String id = c.getString(c.getColumnIndex(DataBase.TEMP_ID));
                    String msg = c.getString(c.getColumnIndex(DataBase.TEMP_MSG));
                    String cat = c.getString(c.getColumnIndex(DataBase.TEMP_CAT));
                    String tit = c.getString(c.getColumnIndex(DataBase.TEMP_TITLE));

                    Model_Temp m = new Model_Temp(id, msg, tit, cat);
                    if (type.equals(cat)) {
                        data.add(m);
                    }
                }
                while (c.moveToNext());
            }

            return data;
        }

        @Override
        protected void onPostExecute(List<Model_Temp> data) {
            super.onPostExecute(data);
            if(data.size()>0) {
                ad = new Temp_List_Adapter(MessageSend.this, data);
                lv.setAdapter(ad);
                lv.setVisibility(View.VISIBLE);
                LinearLayout l = (LinearLayout)findViewById(R.id.notemp);
                l.setVisibility(View.GONE);
            }


            pd.dismiss();
        }
    }

    private void sendMessage(String number, String message) {

        String url = "";
        String lines[] = message.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            sb.append(lines[i]);
            if (i < lines.length - 1) {
                sb.append("%0a");
            }
        }

        String line = sb.toString();

        StringBuilder sb2 = new StringBuilder();
        String sp[] = number.split("\\s");
        for(String s : sp)
        {
            sb2.append(s);
        }

        number = sb2.toString();

        if (number.startsWith("+91")) {
            url = "http://203.129.225.69/API/WebSMS/Http/v1.0a/index.php?username=cbitss&password=123456&sender=CBitss&to=" + number + "&message=" + line + "&reqid=1&format={json|text}&route_id=7";
        } else if (number.startsWith("0")) {
            number = number.replaceFirst("0", "+91");
            url = "http://203.129.225.69/API/WebSMS/Http/v1.0a/index.php?username=cbitss&password=123456&sender=CBitss&to=" + number + "&message=" + line + "&reqid=1&format={json|text}&route_id=7";
        } else {
            url = "http://203.129.225.69/API/WebSMS/Http/v1.0a/index.php?username=cbitss&password=123456&sender=CBitss&to=91" + number + "&message=" + line + "&reqid=1&format={json|text}&route_id=7";
        }

        StringRequest sr = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MessageSend.this, "Message Sent", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        RequestQueue r = Volley.newRequestQueue(this);
        r.add(sr);
    }

}
