package com.codrox.messagetemplate.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.Modals.Model_Temp;
import com.codrox.messagetemplate.R;
import com.codrox.messagetemplate.Adapter.Temp_List_Adapter;

import java.util.ArrayList;
import java.util.List;

public class TemplatesActivity extends AppCompatActivity {

    Model_Temp m;

    RadioGroup rg;
    ListView lv;
    DataBase db;

    List<Model_Temp> text, wap;

    LinearLayout li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);

        db = new DataBase(this);
        text = new ArrayList<>();
        wap = new ArrayList<>();

        rg = (RadioGroup) findViewById(R.id.rg_check);
        lv = (ListView) findViewById(R.id.list_temp);
        li = (LinearLayout) findViewById(R.id.notemp);

        fetchData();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                if (id == R.id.rd_text_temp) {
                    if (text.size() > 0) {
                        Temp_List_Adapter ad = new Temp_List_Adapter(TemplatesActivity.this, text);
                        lv.setAdapter(ad);
                        lv.setVisibility(View.VISIBLE);
                        li.setVisibility(View.GONE);
                    } else {
                        lv.setVisibility(View.GONE);
                        li.setVisibility(View.VISIBLE);
                    }
                } else if (id == R.id.rd_wap_temp) {
                    if (wap.size() > 0) {
                        Temp_List_Adapter ad = new Temp_List_Adapter(TemplatesActivity.this, wap);
                        lv.setAdapter(ad);
                        lv.setVisibility(View.VISIBLE);
                        li.setVisibility(View.GONE);
                    } else {
                        lv.setVisibility(View.GONE);
                        li.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showAlert(position);
                return true;
            }
        });
    }

    private void showAlert(int position) {
        int id = rg.getCheckedRadioButtonId();
        if (id == R.id.rd_text_temp) {
            m = text.get(position);
        } else if (id == R.id.rd_wap_temp) {
            m = wap.get(position);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.template_edit_alert, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(v);

        final AlertDialog ad = builder.create();
        ad.show();

        Button update, delete;
        final EditText ed = v.findViewById(R.id.editText);
        update = v.findViewById(R.id.update);
        delete = v.findViewById(R.id.delete);

        ed.setText(m.getTEMP_MSG());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = ed.getText().toString();
                db.updateTemp(m.getTEMP_ID(), txt, m.getTEMP_TITLE(), m.getTEMP_CAT());
                Toast.makeText(TemplatesActivity.this, "Updated SuccessFull", Toast.LENGTH_SHORT).show();
                fetchData();
                ad.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteTemp(m.getTEMP_ID());
                Toast.makeText(TemplatesActivity.this, "Deleted SuccessFull", Toast.LENGTH_SHORT).show();
                fetchData();
                ad.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchData();
    }

    private void fetchData() {
        Cursor c = db.readAllTemp();
        text.clear();
        wap.clear();
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {

                String id = c.getString(c.getColumnIndex(DataBase.TEMP_ID));
                String msg = c.getString(c.getColumnIndex(DataBase.TEMP_MSG));
                String cat = c.getString(c.getColumnIndex(DataBase.TEMP_CAT));
                String tit = c.getString(c.getColumnIndex(DataBase.TEMP_TITLE));

                Model_Temp m = new Model_Temp(id, msg, tit, cat);

                if (cat.equals(Constants.Text)) {
                    text.add(m);
                } else if (cat.equals(Constants.Whatsapp)) {
                    wap.add(m);
                }
            }
            while (c.moveToNext());
        }

        int id = rg.getCheckedRadioButtonId();

        if (id == R.id.rd_text_temp) {
            if (text.size() > 0) {
                Temp_List_Adapter ad = new Temp_List_Adapter(TemplatesActivity.this, text);
                lv.setAdapter(ad);
                lv.setVisibility(View.VISIBLE);
                li.setVisibility(View.GONE);
            } else {
                lv.setVisibility(View.GONE);
                li.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.rd_wap_temp) {
            if (wap.size() > 0) {
                Temp_List_Adapter ad = new Temp_List_Adapter(TemplatesActivity.this, wap);
                lv.setAdapter(ad);
                lv.setVisibility(View.VISIBLE);
                li.setVisibility(View.GONE);
            } else {
                lv.setVisibility(View.GONE);
                li.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(TemplatesActivity.this, AddTemplateActivity.class);
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }
}
