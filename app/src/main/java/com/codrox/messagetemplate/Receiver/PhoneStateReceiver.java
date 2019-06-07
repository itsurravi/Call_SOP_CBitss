package com.codrox.messagetemplate.Receiver;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.codrox.messagetemplate.Activity.AlertActivity;
import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DataBase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhoneStateReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming;
    private static boolean picked = false;
    private static String savedNumber;
    DataBase db;

    Context c;

    private static String filename;
    private static File audio;
    private static Uri newUri;
    private static MediaRecorder r;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.c = context;
        db = new DataBase(context);
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        }
        else{
            String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int state = 0;
            if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                state = TelephonyManager.CALL_STATE_IDLE;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                state = TelephonyManager.CALL_STATE_OFFHOOK;
            }
            else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                state = TelephonyManager.CALL_STATE_RINGING;
            }
            onCallStateChanged(context, state, number);
        }
    }


    public void onCallStateChanged(Context context, int state, String number) {
        if(lastState == state){
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                savedNumber = number;
                Log.d("Data_ring", number);
//                Toast.makeText(context, "Incoming Call...", Toast.LENGTH_SHORT).show();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    savedNumber=number;
                }
                else {
                    picked=true;
                }

                String n[] = number.split("\\s+");
                StringBuilder ab = new StringBuilder();
                for(String in : n)
                {
                    ab.append(in);
                }

                number = ab.toString();

                recordingStart();

                db.insertCall(number,"","", String.valueOf(System.currentTimeMillis()), Constants.Status_Pending,"", audio.getAbsolutePath(),Constants.offline);

                Log.d("Data_offhook", number);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Cursor c = db.readAllCalls();
                int id = c.getCount();
                Log.d("Data_idle", number);
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_OFFHOOK && picked){
//                    Toast.makeText(context, "Incoming Call Ended...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, AlertActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.Number, savedNumber);
                    intent.putExtra(Constants.ID, String.valueOf(id));
                    context.startActivity(intent);
                }
                else if(!isIncoming){
//                    Toast.makeText(context, "Outgoing    Call Ended...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,AlertActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constants.Number, savedNumber);
                    intent.putExtra(Constants.ID, String.valueOf(id));
                    context.startActivity(intent);
                }

                recordingStop();

                break;
        }
        lastState = state;
    }

    private void recordingStart() {
        File dir = new File(Environment.getExternalStorageDirectory() + "/CallSopRecordings/");
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            long time = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("MMM-dd-yyyy-HH:mm:ss");
            Date resultdate = new Date(time);
            filename = savedNumber + "-" + sdf.format(resultdate);
            audio = new File(dir, filename + ".mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        r = new MediaRecorder();
        r.setAudioSource(MediaRecorder.AudioSource.MIC);
        r.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        r.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        r.setAudioChannels(1);
        r.setAudioSamplingRate(44100);
        r.setAudioEncodingBitRate(192000);
        r.setOutputFile(audio.getAbsolutePath());
        try {
            r.prepare();
            r.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recordingStop() {
        r.stop();
        r.release();
        addRecordingToMediaLibrary();
        r=null;
    }

    private void addRecordingToMediaLibrary() {
        ContentValues values = new ContentValues(4);
        long current = System.currentTimeMillis();
        values.put(MediaStore.Audio.Media.TITLE, "audio" + audio.getName());
        values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.DATA, audio.getAbsolutePath());

        //creating content resolver and storing it in the external content uri
        ContentResolver contentResolver = c.getContentResolver();
        Uri base = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        newUri = contentResolver.insert(base, values);

        //sending broadcast message to scan the media file so that it can be available
        c.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));
//        Toast.makeText(c, "Added File " + newUri, Toast.LENGTH_LONG).show();
    }

}