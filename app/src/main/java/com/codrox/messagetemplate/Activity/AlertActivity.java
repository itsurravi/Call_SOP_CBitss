package com.codrox.messagetemplate.Activity;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.codrox.messagetemplate.Constants;
import com.codrox.messagetemplate.DB.DataBase;
import com.codrox.messagetemplate.FileHandle;
import com.codrox.messagetemplate.HomeWatcher;
import com.codrox.messagetemplate.Modals.FileInfo;
import com.codrox.messagetemplate.Prefrence;
import com.codrox.messagetemplate.R;
import com.codrox.messagetemplate.Receiver.FileUploadingService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AlertActivity extends AppCompatActivity {

    TextView tv;
    Button text, wap;
    String id;
    String num;

    ProgressDialog pd;

    List<FileInfo> fileData;

    Prefrence sp;

    DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        Intent i = getIntent();
        num = i.getStringExtra(Constants.Number);
        id = i.getStringExtra(Constants.ID);

        tv = findViewById(R.id.number);
        text = findViewById(R.id.send_text);
        wap = findViewById(R.id.send_wap);
        fileData = new ArrayList<>();
        db = new DataBase(this);

        tv.setText(num);

        homeWatcher();

        sp = new Prefrence(this);

        new DataInfo().execute();

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlertActivity.this, MessageSend.class);
                i.putExtra(Constants.ID, id);
                i.putExtra(Constants.Number, num);
                i.putExtra(Constants.TYPE, Constants.Text);
                startActivity(i);
            }
        });

        wap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlertActivity.this, MessageSend.class);
                i.putExtra(Constants.ID, id);
                i.putExtra(Constants.Number, num);
                i.putExtra(Constants.TYPE, Constants.Whatsapp);
                startActivity(i);
            }
        });

        ComponentName componentName = new ComponentName(this, FileUploadingService.class);

        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        try {

            List<JobInfo> l = scheduler.getAllPendingJobs();

            if (l.size() <= 0) {

                JobInfo info = new JobInfo.Builder(121, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setMinimumLatency(7000)
                        .build();

                int result = scheduler.schedule(info);

                if (result == JobScheduler.RESULT_SUCCESS) {
                    Log.d("JOBSCHEDULE", "Job Scheduled");
                } else {
                    Log.d("JOBSCHEDULE", "Job Scheduling Failed");
                }
            }

        }
        catch(Exception e)
        {
            Log.d("ErrorLogs", String.valueOf(e));
        }
        getDateAndDeleteRecord();
    }

    private void homeWatcher() {
        final HomeWatcher mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                finish();
                mHomeWatcher.stopWatch();
            }
            @Override
            public void onHomeLongPressed() {
            }
        });
        mHomeWatcher.startWatch();
    }

    private void getDateAndDeleteRecord() {
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        if(date==1)
        {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    db.deleteOldRecord(String.valueOf(System.currentTimeMillis()));
                }
            }).start();
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

    private class DataInfo extends AsyncTask<Void, Void, File> {


        String outFileName, name;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(AlertActivity.this);
            pd.setMessage("Please Wait...");
            pd.show();
        }

        @Override
        protected File doInBackground(Void... voids) {

            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI,
                    null, null, null, CallLog.Calls.DATE + " DESC");
            int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
            cursor.moveToNext();

            String callType = cursor.getString(type);
            String callDuration = cursor.getString(duration);

            int dircode = Integer.parseInt(callType);

            /*
            String dir = null;
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }*/

            if(dircode==CallLog.Calls.MISSED_TYPE)
            {
                return null;
            }
            else
            {
                if(Integer.parseInt(callDuration)>0)
                {

                    FileHandle fd = new FileHandle(AlertActivity.this);
                    File file = fd.getFiles(new File(sp.getAudioPath()));
                    try {
                        if(file!=null)
                        copyDataBase(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return file;
                }
            }
            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(File file) {
            super.onPostExecute(file);
            if(file!=null)
            {
                Log.d("FileCopy", "Done");
            }
            pd.dismiss();
        }

        private void copyDataBase(File file) throws IOException {

            // Open your local db as the input stream
            InputStream myInput = new FileInputStream(file.getAbsolutePath());

            String filename = file.getName();
            name = filename.substring(0, filename.indexOf("."));

            outFileName = Environment.getExternalStorageDirectory() + "/CallSopRecordings/" + name + ".mp3";

            Log.d("PathNew", outFileName);

            // Open the empty db as the output stream
            new File(outFileName).createNewFile();
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            db.updateAudioPath(id, outFileName);

            if (file.delete()) {
                Log.d("PathNew", "Changed");
            } else {
                Log.d("PathNew", "Not Changed");
            }

//            addRecordingToMediaLibrary();

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
    }
}
