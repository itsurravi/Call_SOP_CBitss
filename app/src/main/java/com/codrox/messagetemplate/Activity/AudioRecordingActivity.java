package com.codrox.messagetemplate.Activity;

import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.codrox.messagetemplate.Adapter.Audio_Recording_Adapter;
import com.codrox.messagetemplate.DB.DataBase;
import com.codrox.messagetemplate.Modals.Modal_Audio_Recording;
import com.codrox.messagetemplate.R;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioRecordingActivity extends AppCompatActivity implements Audio_Recording_Adapter.onItemClick, Runnable {

    RecyclerView rv;
    TextView timer;
    SeekBar seekBar;

    DataBase db;
    List<Modal_Audio_Recording> data;
    String callNumber;

    MediaPlayer mp;
    boolean wasPlaying;
    int lastpos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recording);

        db = new DataBase(this);

        rv = findViewById(R.id.audio_list);
        timer = findViewById(R.id.timer);
        seekBar = findViewById(R.id.seekbar);

        data = new ArrayList<>();
        mp = new MediaPlayer();

        wasPlaying = false;

        callNumber = getIntent().getExtras().getString("number");

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);

        rv.setLayoutManager(lm);
        rv.setItemAnimator(new DefaultItemAnimator());

        new fetchRecord().execute();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                timer.setVisibility(View.VISIBLE);

                int x = (int) Math.ceil(progress / 1000f);

                if (x < 10)
                    timer.setText("0:0" + x);
                else
                    timer.setText("0:" + x);

                if (progress > 0 && mp != null && !mp.isPlaying()) {
                    clearMediaPlayer();
                    AudioRecordingActivity.this.seekBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                timer.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mp != null && mp.isPlaying()) {
                    mp.seekTo(seekBar.getProgress());
                }
            }
        });
    }

    private void clearMediaPlayer() {
        mp.stop();
        mp.release();
        mp = null;
        wasPlaying = false;
    }

    public void run() {
        int currentPosition = mp.getCurrentPosition();
        int total = mp.getDuration();

        while (mp != null && mp.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mp.getCurrentPosition();
            } catch (Exception e) {
                return;
            }
            seekBar.setProgress(currentPosition);
        }
    }

    public void playSong(String source) {
        try {
            if (mp != null && mp.isPlaying()) {
                clearMediaPlayer();
                seekBar.setProgress(0);
                wasPlaying = true;
            }

            if (!wasPlaying) {

                if (mp == null) {
                    mp = new MediaPlayer();
                }

                mp.setDataSource(source);
                mp.prepare();
                mp.setVolume(0.5f, 0.5f);
                mp.setLooping(false);
                seekBar.setMax(mp.getDuration());

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer m) {
                        clearMediaPlayer();
                    }
                });

                mp.start();
                new Thread(this).start();

            }
            wasPlaying = true;
        } catch (Exception e) {
            e.printStackTrace();
            try
            {
                final FileInputStream fis = new FileInputStream(source);
                if (mp != null && mp.isPlaying()) {
                    clearMediaPlayer();
                    seekBar.setProgress(0);
                    wasPlaying = true;
                }

                if (!wasPlaying) {

                    if (mp == null) {
                        mp = new MediaPlayer();
                    }

                    mp.setDataSource(fis.getFD());
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mp.prepare();
                    mp.setVolume(0.5f, 0.5f);
                    mp.setLooping(false);
                    seekBar.setMax(mp.getDuration());

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer m) {
                            clearMediaPlayer();
                        }
                    });

                    mp.start();
                    fis.close();
                    new Thread(this).start();

                }
                wasPlaying = true;

            }
            catch (Exception ex)
            {

            }
        }
    }

    @Override
    public void itemClick(int position) {
        if (lastpos == position && wasPlaying) {
            clearMediaPlayer();
        } else {
            if (wasPlaying) {
                clearMediaPlayer();
            }
            playSong(data.get(position).getAudio_path());
        }
        lastpos = position;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (wasPlaying)
            clearMediaPlayer();
    }

    class fetchRecord extends AsyncTask<Void, Void, ArrayList<Modal_Audio_Recording>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<Modal_Audio_Recording> doInBackground(Void... voids) {
            ArrayList<Modal_Audio_Recording> d = new ArrayList<>();
            Cursor c = db.readNumberData(callNumber);
            c.moveToFirst();
            do {
                String id = c.getString(c.getColumnIndex(DataBase.CALL_ID));
                String audio_path = c.getString(c.getColumnIndex(DataBase.CALL_AUDIO));
                String date = c.getString(c.getColumnIndex(DataBase.CALL_DATE));
                String status = c.getString(c.getColumnIndex(DataBase.CALL_AUDIO_STATUS));
                Modal_Audio_Recording m = new Modal_Audio_Recording(id, audio_path, date, status);

                d.add(m);
            }
            while (c.moveToNext());

            c.close();

            return d;
        }

        @Override
        protected void onPostExecute(ArrayList<Modal_Audio_Recording> list) {
            super.onPostExecute(list);

            data = list;
            Collections.reverse(data);
            Audio_Recording_Adapter ad = new Audio_Recording_Adapter(AudioRecordingActivity.this, data);
            rv.setAdapter(ad);

            ad.setOnItemClick(AudioRecordingActivity.this);

        }
    }

}