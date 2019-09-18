package com.codrox.messagetemplate.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codrox.messagetemplate.Adapter.Call_List_Adapter;
import com.codrox.messagetemplate.DB.DataBase;
import com.codrox.messagetemplate.Modals.Modal_Call;
import com.codrox.messagetemplate.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CallDataActivity extends AppCompatActivity implements Call_List_Adapter.OnRvItemClickListener {

    RecyclerView lv;
    ProgressDialog pd;
    List<Modal_Call> data;
    List<String> tags;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lv = (RecyclerView) findViewById(R.id.list);
        data = new ArrayList<>();
        tags = new ArrayList<>();

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        lv.setLayoutManager(lm);

        lv.setItemAnimator(new DefaultItemAnimator());

        db = new DataBase(this);

        try
        {
            new fetchCall().execute();
        }
        catch (Exception e)
        {
            Toast.makeText(this, "Please Open App Again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(pd.isShowing())
        {
            pd.dismiss();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRvItemClick(int position) {

        Modal_Call m = data.get(position);

        Intent i = new Intent(this, MessageInfoActivity.class);
        i.putExtra("CALL_NUMBER", m.getCALL_NUMBER());
        i.putExtra("CALL_DATE", m.getCALL_DATE());
        i.putExtra("CALL_NAME", m.getCALL_NAME());

        startActivity(i);
    }

    @Override
    public void onRvItemLongClick(int position) {
        final Modal_Call m = data.get(position);
        View v = LayoutInflater.from(this).inflate(R.layout.editname_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);
        final AlertDialog ad = builder.create();
        ad.show();

        final EditText ed = v.findViewById(R.id.edit_name);
        Button b = v.findViewById(R.id.savename);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ed.getText().toString();
                if(name.isEmpty())
                {
                    ed.setError("Please Fill this Field");
                    ed.requestFocus();
                }
                else {
                    db.updateCallName(name, m.getCALL_NUMBER());
                    ad.dismiss();
                    new fetchCall().execute();
                }
            }
        });
    }

    class fetchCall extends AsyncTask<Void, Void, List<Modal_Call>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(CallDataActivity.this);
            pd.setMessage("Wait");
            pd.setCancelable(false);
            pd.show();
        }


        @Override
        protected List<Modal_Call> doInBackground(Void... voids) {
            data.clear();
            tags.clear();

            Cursor c = db.readDistinctCalls();
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();
                do {
                    String NUMBER = c.getString(c.getColumnIndex(DataBase.CALL_NUMBER));
                    String DATE = c.getString(c.getColumnIndex(DataBase.CALL_DATE));
                    String ID = c.getString(c.getColumnIndex(DataBase.CALL_ID));

                    String name = "";
                    Cursor d = db.readNumberData(NUMBER);
                    if(d != null && d.getCount()>0)
                    {
                        d.moveToFirst();
                        do {
                            String n = d.getString(d.getColumnIndex(DataBase.CALL_NAME));
                            if(!n.equals(""))
                            {
                                name = n;
                            }
                        }
                        while (d.moveToNext());
                        d.close();
                    }

                    Modal_Call m = new Modal_Call(ID, NUMBER, DATE, name);

                    data.add(m);
                }
                while (c.moveToNext());
            }
            Collections.reverse(data);

            for(int i = 0;i<data.size();i++) {
                String num = data.get(i).getCALL_NUMBER();
                Cursor cr = db.readTag(num);
                if(cr !=null && cr.getCount()>0)
                {
                    StringBuilder sb = new StringBuilder();
                    cr.moveToFirst();
                    do {
                        String tag = cr.getString(cr.getColumnIndex(DataBase.TAG_NAME));
                        sb.append(tag+", ");
                    }
                    while (cr.moveToNext());

                    cr.close();

                    String tag = sb.toString();
                    tags.add(tag);
                }
                else
                {
                    tags.add("");
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(List<Modal_Call> modal_calls) {
            super.onPostExecute(modal_calls);
            Call_List_Adapter ad = new Call_List_Adapter(CallDataActivity.this, data, tags);
            ad.setOnItemClick(CallDataActivity.this);
            ad.setOnItemLongClick(CallDataActivity.this);
            pd.dismiss();
            lv.setAdapter(ad);
        }
    }

}
