package com.codrox.messagetemplate.Receiver;

import android.app.ProgressDialog;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.codrox.messagetemplate.DB.DataBase;
import com.codrox.messagetemplate.Modals.Modal_Call;
import com.codrox.messagetemplate.Modals.Remarks;
import com.codrox.messagetemplate.Modals.Tags;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUploadingService extends JobService {

    private static final String UPLOAD_URL = "http://fossfoundation.com/SOP/newone.php";
    private static final String UPLOAD_URLR = "http://fossfoundation.com/SOP/remark.php";
    private static final String UPLOAD_URLT = "http://fossfoundation.com/SOP/tag.php";

    String TAG = "JobChecking";

    List<Modal_Call> call;
    List<Remarks> remarks;
    List<Tags> tags;
    DataBase db;

    JobParameters params;

    @Override
    public boolean onStartJob(JobParameters params) {
        call = new ArrayList<>();
        remarks = new ArrayList<>();
        tags = new ArrayList<>();

        db = new DataBase(getApplicationContext());

        Log.d(TAG, "Job Started");

        this.params = params;

        new Thread(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }).start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public void getData() {
//        Toast.makeText(this, "check", Toast.LENGTH_SHORT).show();

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
                                Log.d("Errorrr_1", exception.getMessage());
                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                db.updateAudioStatus(m.getCALL_ID());
                                uploadMultipart(pos + 1);
                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {

                            }
                        })
                        .startUpload();
            } catch (Exception exc) {
//                Log.d("Errorrr_1.1", exc.getMessage());
                Log.d("Errorrr_1.1", String.valueOf(exc.getStackTrace()[0].getMethodName()));
                uploadMultipart(pos + 1);

            } finally {

            }
        }

    }

    public void uploadMultipartRemarks(final int pos) {
        Log.d("tag_2", "hello");
        if (pos >= 0 && remarks.size() > pos) {
            //getting name for the image
            final Remarks m = remarks.get(pos);

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
                                Log.d("Errorrr_2", exception.getMessage());
                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                db.updateRemarks(m.getId());
                                uploadMultipartRemarks(pos + 1);
                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {
                            }
                        })
                        .startUpload();

                //Starting the upload


            } catch (Exception exc) {
                Log.d("Errorrr_2.1", exc.getMessage());
                uploadMultipartRemarks(pos + 1);
            } finally {

            }
        }

    }

    public void uploadMultipartTags(final int pos) {
        Log.d("tag_3", "hello");
        if (pos >= 0 && tags.size() > pos) {
            //getting name for the image
            final Tags t = tags.get(pos);

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
                                Log.d("Errorrr_3", exception.getMessage());
                            }

                            @Override
                            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                                db.updateTags(t.getTAG_ID());
                                uploadMultipartTags(pos + 1);
                            }

                            @Override
                            public void onCancelled(Context context, UploadInfo uploadInfo) {
                            }
                        })
                        .startUpload();
                //Starting the upload
            } catch (Exception exc) {
                Log.d("Errorrr_3.1", exc.getMessage());
                uploadMultipartTags(pos + 1);
            } finally {
            }
        } else {
            jobFinished(params, false);
        }
    }
}
