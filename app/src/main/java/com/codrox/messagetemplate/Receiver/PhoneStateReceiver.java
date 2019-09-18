package com.codrox.messagetemplate.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.codrox.messagetemplate.Activity.AlertActivity;
import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DB.DataBase;

public class PhoneStateReceiver extends BroadcastReceiver {

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming;
    private static boolean picked = false;
    private static String savedNumber;
    DataBase db;

    Context c;

    /*private static String filename;
    private static File audio;
    private static Uri newUri;
    private static MediaRecorder r;*/

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

//                recordingStart();

                db.insertCall(number,"","", String.valueOf(System.currentTimeMillis()), Constants.Status_Pending,"", "",Constants.offline);

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

//                recordingStop();

                break;
        }
        lastState = state;
    }


}