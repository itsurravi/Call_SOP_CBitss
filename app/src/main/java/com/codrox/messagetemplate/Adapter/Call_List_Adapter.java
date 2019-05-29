package com.codrox.messagetemplate.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codrox.messagetemplate.Activity.CallDataActivity;
import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.Modals.Modal_Call;
import com.codrox.messagetemplate.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

public class Call_List_Adapter extends RecyclerView.Adapter<Call_List_Adapter.ListViewHolder> {

    Context c;
    List<Modal_Call> data;
    List<String> tags;

    OnRvItemClickListener click;

    public Call_List_Adapter(Context c, List<Modal_Call> data, List<String> tags) {
        this.c = c;
        this.data = data;
        this.tags = tags;
    }

    private String setDate(String call_date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(call_date));
        return formatter.format(calendar.getTime());
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater li = LayoutInflater.from(c);
        View v = li.inflate(R.layout.call_list_adapter, viewGroup, false);

        ListViewHolder holder = new ListViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder listViewHolder, int position) {
        listViewHolder.date.setText(setDate(data.get(position).getCALL_DATE()));
        if(!data.get(position).getCALL_NAME().equals("")) {
            listViewHolder.num.setText(data.get(position).getCALL_NAME());
        }
        else{
            listViewHolder.num.setText(data.get(position).getCALL_NUMBER());
        }

        if(tags.get(position).equals(""))
        {
            listViewHolder.mTagGroup.setVisibility(View.GONE);
        }
        else {

            String ar[] = tags.get(position).split(", ");

            listViewHolder.mTagGroup.setTags(ar);
        }
        listViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onRvItemClick(listViewHolder.getAdapterPosition());
            }
        });
        listViewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                click.onRvItemLongClick(listViewHolder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        TextView num;
        TextView date;
        TextView tags;
        /*ImageView s_icon;
        TextView status;
        TextView msg;
        TextView wap;*/
        LinearLayout layout;
        TagGroup mTagGroup;

        public ListViewHolder(@NonNull View convertView) {
            super(convertView);
            num = convertView.findViewById(R.id.number);
            date = convertView.findViewById(R.id.date);
//            tags = convertView.findViewById(R.id.tags);
            /*s_icon = convertView.findViewById(R.id.s_color);
            status = convertView.findViewById(R.id.status);
            msg = convertView.findViewById(R.id.txt_msg);
            wap = convertView.findViewById(R.id.wa_msg);*/
            layout = convertView.findViewById(R.id.layout);
            mTagGroup = (TagGroup) convertView.findViewById(R.id.tag_group);
        }
    }

    public void setOnItemClick(OnRvItemClickListener click) {
        this.click = click;
    }

    public void setOnItemLongClick(OnRvItemClickListener click) {
        this.click = click;
    }

    public interface OnRvItemClickListener {
        void onRvItemClick(int position);

        void onRvItemLongClick(int position);
    }
}
