package com.codrox.messagetemplate.Adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.Modals.Modal_Audio_Recording;
import com.codrox.messagetemplate.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Audio_Recording_Adapter extends RecyclerView.Adapter<Audio_Recording_Adapter.MyHolder> {

    Context c;
    List<Modal_Audio_Recording> recordings;

    onItemClick click;

    public Audio_Recording_Adapter(Context c, List<Modal_Audio_Recording> recordings) {
        this.c = c;
        this.recordings = recordings;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(c).inflate(R.layout.audio_list_adapter, viewGroup, false);
        MyHolder h = new MyHolder(v);
        return h;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
        final Modal_Audio_Recording m = recordings.get(i);
        myHolder.title.setText(setDate(m.getDate()));
        myHolder.audio_status.setText(m.getAudio_status());
        if (m.getAudio_status().equals(Constants.online)) {
            myHolder.img_audio_status.setImageResource(R.drawable.ic_sent_circle);
        } else {
            myHolder.img_audio_status.setImageResource(R.drawable.ic_pending_circle);
        }
        myHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.itemClick(myHolder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    private String setDate(String call_date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy -- hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(call_date));
            return formatter.format(calendar.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {

        LinearLayout layout;
        TextView title, audio_status;
        ImageView img_audio_status;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.audio_layout);
            title = itemView.findViewById(R.id.audio_filename);
            img_audio_status = itemView.findViewById(R.id.img_audio_status);
            audio_status = itemView.findViewById(R.id.audio_status);
        }
    }

    public interface onItemClick{
        void itemClick(int position);
    }
    public void setOnItemClick(onItemClick click) {
        this.click = click;
    }
}
