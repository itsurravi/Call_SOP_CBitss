package com.codrox.messagetemplate.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.codrox.messagetemplate.Modals.Model_Temp;
import com.codrox.messagetemplate.R;

import java.util.List;

public class Temp_List_Adapter extends BaseAdapter {

    Context c;
    List<Model_Temp> data;

    public Temp_List_Adapter(Context context, List<Model_Temp> data) {
        c=context;
        this.data=data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = LayoutInflater.from(c);
        convertView = li.inflate(R.layout.template_list_adapter, null);

        TextView tv = (TextView)convertView.findViewById(R.id.template);
        TextView tit = (TextView)convertView.findViewById(R.id.title);

        CardView cd = (CardView)convertView.findViewById(R.id.cardView);

        if(data.get(position).getIsSelected())
        {
            cd.setCardBackgroundColor(c.getResources().getColor(R.color.cardBackground));
            tv.setTextColor(c.getResources().getColor(R.color.textColorSelected));
            tit.setTextColor(c.getResources().getColor(R.color.textColorSelected));
        }

        tv.setText(data.get(position).getTEMP_MSG());
        tit.setText(data.get(position).getTEMP_TITLE());

        return convertView;
    }
}
