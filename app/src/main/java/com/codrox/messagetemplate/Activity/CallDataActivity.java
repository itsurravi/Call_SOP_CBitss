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

import com.codrox.messagetemplate.Adapter.Call_List_Adapter;
import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.Modals.Modal_Call;
import com.codrox.messagetemplate.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CallDataActivity extends AppCompatActivity implements Call_List_Adapter.OnRvItemClickListener {

    RecyclerView lv;

    List<Modal_Call> data;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_data);

        lv = (RecyclerView) findViewById(R.id.list);
        data = new ArrayList<>();

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        lv.setLayoutManager(lm);

        lv.setItemAnimator(new DefaultItemAnimator());

        db = new DataBase(this);
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
        ProgressDialog pd;

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
                    }

                    Modal_Call m = new Modal_Call(ID, NUMBER, DATE, name);

                    data.add(m);
                }
                while (c.moveToNext());
            }
            Collections.reverse(data);
            return data;
        }

        @Override
        protected void onPostExecute(List<Modal_Call> modal_calls) {
            super.onPostExecute(modal_calls);
            Call_List_Adapter ad = new Call_List_Adapter(CallDataActivity.this, modal_calls);
            ad.setOnItemClick(CallDataActivity.this);
            ad.setOnItemLongClick(CallDataActivity.this);
            pd.dismiss();
            lv.setAdapter(ad);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new fetchCall().execute();
    }
}
