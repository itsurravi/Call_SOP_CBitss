package com.codrox.messagetemplate.Activity;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codrox.messagetemplate.DB.DataBase;
import com.codrox.messagetemplate.FileHandle;
import com.codrox.messagetemplate.Modals.Modal_Call;
import com.codrox.messagetemplate.Modals.Remarks;
import com.codrox.messagetemplate.Modals.Tags;
import com.codrox.messagetemplate.Prefrence;
import com.codrox.messagetemplate.R;
import com.codrox.messagetemplate.Receiver.FileUploadingService;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    DataBase db;
    String UPLOAD_URL = "http://fossfoundation.com/SOP/newone.php";
    String UPLOAD_URLR = "http://fossfoundation.com/SOP/remark.php";
    String UPLOAD_URLT = "http://fossfoundation.com/SOP/tag.php";

    Prefrence sp;
    List<Modal_Call> call;
    List<Remarks> remarks;
    List<Tags> tags;
    Button b1, b2;
    public final int PERMISSION_CODE = 12345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        call = new ArrayList<>();
        remarks = new ArrayList<>();
        tags = new ArrayList<>();

        db = new DataBase(this);

        b1 = findViewById(R.id.btn_calls);
        b2 = findViewById(R.id.btn_templates);
        sp = new Prefrence(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }

        String manufacturer = Build.MANUFACTURER;

        if ("xiaomi".equalsIgnoreCase(manufacturer)
//                || "oppo".equalsIgnoreCase(manufacturer)
                || "vivo".equalsIgnoreCase(manufacturer)
                || "Letv".equalsIgnoreCase(manufacturer)
                || "Honor".equalsIgnoreCase(manufacturer)) {

            if (!sp.checkstart()) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Settings");
                b.setMessage("Please Enable AutoStart for this App\n (Call SOP) for its Proper Funtionality");
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        openSettings();
                        sp.setStart();
                        dialog.dismiss();
                    }
                });

                AlertDialog ad = b.create();
                ad.setCancelable(false);
                ad.show();
            }
        }
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, CallDataActivity.class);
                startActivity(i);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, TemplatesActivity.class);
                startActivity(i);
            }
        });
    }

    public void openSettings() {

        String manufacturer = Build.MANUFACTURER;

        try {
            Intent intent = new Intent();
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            }
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Checkingerror", e.getMessage());
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result4 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        int result5 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CALL_LOG);
        int result6 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CALL_LOG);

        return result == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED
                && result5 == PackageManager.PERMISSION_GRANTED
                && result6 == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS, READ_PHONE_STATE,
                READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, READ_CALL_LOG, WRITE_CALL_LOG}, PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {

                    boolean sms = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean phone = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean storage = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean storage2 = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean recording = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean call_log = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean call_log2 = grantResults[6] == PackageManager.PERMISSION_GRANTED;

                    if (sms && phone && storage && storage2 && recording && call_log && call_log2) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sync) {
//            getData();
            ComponentName componentName = new ComponentName(this, FileUploadingService.class);

            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

            try {

                List<JobInfo> l = scheduler.getAllPendingJobs();

                if (l.size() > 0) {
                    scheduler.cancelAll();
                }

                JobInfo info = new JobInfo.Builder(122, componentName)
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

            } catch (Exception e) {
                Log.d("JobError", e.toString());
            }
        } else if (item.getItemId() == R.id.path) {
            choosePath();
        } else if (item.getItemId() == R.id.deleteRecord) {

            Toast.makeText(this, "Deletion of Old Record started", Toast.LENGTH_SHORT).show();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    db.deleteOldRecord(String.valueOf(System.currentTimeMillis()));
                }
            }).start();
        }
        return super.onOptionsItemSelected(item);
    }

    //Path Choose for audio recording Code

    private void choosePath() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9999) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.i("Test", "Result URI " + data.getData());
                    Uri uri = data.getData();
                    Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                            DocumentsContract.getTreeDocumentId(uri));
                    String path = FileHandle.getPath(this, docUri);
                    sp.setAudioPath(path);
                    Log.d("Test", path);
                } catch (Exception e) {
                    Toast.makeText(this, "Path is Not Valid", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please Choose a Path", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*//Data Upload to DB Server

    public void getData() {
        Toast.makeText(this, "check", Toast.LENGTH_SHORT).show();

        try {
            Cursor cr = db.getUnsyncedCalls();
            call.clear();
            if (cr != null && cr.getCount() > 0) {
                cr.moveToFirst();

                do {
                    String CALL_ID = cr.getString(cr.getColumnIndex(DataBase.CALL_ID));
                    String CALL_NUMBER = cr.getString(cr.getColumnIndex(DataBase.CALL_NUMBER));
                    String CALL_MSG = cr.getString(cr.getColumnIndex(DataBase.CALL_MSG));
                    String CALL_WAP = cr.getString(cr.getColumnIndex(DataBase.CALL_WAP));
                    String CALL_DATE = cr.getString(cr.getColumnIndex(DataBase.CALL_DATE));
                    String CALL_STATUS = cr.getString(cr.getColumnIndex(DataBase.CALL_STATUS));
                    String CALL_NAME = cr.getString(cr.getColumnIndex(DataBase.CALL_NAME));
                    String CALL_AUDIO = cr.getString(cr.getColumnIndex(DataBase.CALL_AUDIO));
                    String CALL_AUDIO_STATUS = cr.getString(cr.getColumnIndex(DataBase.CALL_AUDIO_STATUS));

                    Modal_Call m = new Modal_Call(CALL_ID, CALL_NUMBER, CALL_MSG, CALL_WAP, CALL_DATE, CALL_STATUS, CALL_NAME, CALL_AUDIO, CALL_AUDIO_STATUS);
                    call.add(m);
                }

                while (cr.moveToNext());
            }

            Cursor c = db.getUnsyncedRemarks();
            remarks.clear();
            if (c != null && c.getCount() > 0) {
                c.moveToFirst();

                do {
                    String REMARKS_ID = c.getString(c.getColumnIndex(DataBase.REMARKS_ID));
                    String REMARKS = c.getString(c.getColumnIndex(DataBase.REMARKS));
                    String NUMBER = c.getString(c.getColumnIndex(DataBase.NUMBER));
                    String REMARKS_DATE = c.getString(c.getColumnIndex(DataBase.REMARKS_DATE));

                    Remarks m = new Remarks(REMARKS_ID, REMARKS, NUMBER, REMARKS_DATE);
                    remarks.add(m);
                }

                while (c.moveToNext());
            }


            Cursor ct = db.getUnsyncedTags();
            tags.clear();
            if (ct != null && ct.getCount() > 0) {
                ct.moveToFirst();

                do {
                    String TAG_ID = ct.getString(ct.getColumnIndex(DataBase.TAG_ID));
                    String TAG_NUMBER = ct.getString(ct.getColumnIndex(DataBase.TAG_NUMBER));
                    String TAG_NAME = ct.getString(ct.getColumnIndex(DataBase.TAG_NAME));


                    Tags t = new Tags(TAG_ID, TAG_NUMBER, TAG_NAME);
                    tags.add(t);
                }

                while (ct.moveToNext());
            }


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.d("position", String.valueOf(call.size()));

        uploadMultipart(0);
        uploadMultipartTags(0);
        uploadMultipartRemarks(0);

//            Toast.makeText(this, call.get(0).getCALL_NUMBER(), Toast.LENGTH_SHORT).show();

    }

    public void uploadMultipart(final int pos) {
        Log.d("tag_1", "hello");
        if (pos >= 0 && call.size() > pos) {
            //getting name for the image
            final Modal_Call m = call.get(pos);

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setCancelable(false);
            pd.setMessage("Uploading Audio no. " + (pos + 1) + " out of " + call.size());
            pd.show();
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                        .addFileToUpload(m.getCALL_AUDIO(), "audio")
                        .addParameter("id", m.getCALL_ID())
                        .addParameter("name", m.getCALL_NAME())
                        .addParameter("number", m.getCALL_NUMBER())
                        .addParameter("msg", m.getCALL_MSG())
                        .addParameter("wap", m.getCALL_WAP())
                        .addParameter("date", m.getCALL_DATE())
                        .addParameter("status", m.getCALL_STATUS())
                        .addParameter("audio_status", m.getCALL_AUDIO_STATUS())
                        .addParameter("users", "Ritika")
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {

                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                pd.dismiss();
                                Log.d("Errorrr_1", exception.getMessage());
                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                db.updateAudioStatus(m.getCALL_ID());
                                pd.dismiss();
                                uploadMultipart(pos + 1);
                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {
                                pd.dismiss();
                            }
                        })
                        .startUpload();
            } catch (Exception exc) {
                Log.d("Errorrr_1.1", exc.getMessage());
                pd.dismiss();
            } finally {

            }
        }

    }

    public void uploadMultipartRemarks(final int pos) {
        Log.d("tag_2", "hello");
        if (pos >= 0 && remarks.size() > pos) {
            //getting name for the image
            final Remarks m = remarks.get(pos);

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setCancelable(false);
            pd.setMessage("Uploading Remarks no. " + (pos + 1) + " out of " + remarks.size());
            pd.show();
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_URLR)

                        .addParameter("r_id", m.getId())
                        .addParameter("r_remark", m.getRemarks())
                        .addParameter("r_number", m.getNumber())
                        .addParameter("r_date", m.getRemarks_date())
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {

                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                pd.dismiss();
                                Log.d("Errorrr_2", exception.getMessage());
                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                db.updateRemarks(m.getId());
                                pd.dismiss();
                                uploadMultipartRemarks(pos + 1);
                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {
                                pd.dismiss();
                            }
                        })
                        .startUpload();

                //Starting the upload


            } catch (Exception exc) {
                Log.d("Errorrr_2.1", exc.getMessage());
                pd.dismiss();
            } finally {

            }
        }

    }

    public void uploadMultipartTags(final int pos) {
        Log.d("tag_3", "hello");
        if (pos >= 0 && tags.size() > pos) {
            //getting name for the image
            final Tags t = tags.get(pos);

            final ProgressDialog pd = new ProgressDialog(this);
            pd.setCancelable(false);
            pd.setMessage("Uploading File no. " + (pos + 1) + " out of " + tags.size());
            pd.show();
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOAD_URLT)
                        .addParameter("t_id", t.getTAG_ID())
                        .addParameter("t_name", t.getTAG_NAME())
                        .addParameter("t_number", t.getTAG_NUMBER())
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .setDelegate(new UploadStatusDelegate() {
                            @Override
                            public void onProgress(Context context, UploadInfo uploadInfo) {

                            }

                            @Override
                            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                                pd.dismiss();
                                Log.d("Errorrr_3", exception.getMessage());
                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                db.updateTags(t.getTAG_ID());
                                pd.dismiss();
                                uploadMultipartTags(pos + 1);
                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {
                                pd.dismiss();
                            }
                        })
                        .startUpload();
                //Starting the upload
            } catch (Exception exc) {
                Log.d("Errorrr_3.1", exc.getMessage());
                pd.dismiss();
            } finally {
                Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();
            }
        }
    }*/
}