package com.codrox.messagetemplate.Activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.Modals.Modal_Call;
import com.codrox.messagetemplate.Prefrence;
import com.codrox.messagetemplate.R;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    DataBase db;
    Button b1, b2;
    String UPLOAD_URL = "http://fossfoundation.com/SOP/newone.php";
    public final int PERMISSION_CODE = 12345;

    Prefrence sp;
    List<Modal_Call> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        call = new ArrayList<>();
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

        return result == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED
                && result4 == PackageManager.PERMISSION_GRANTED
                && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS, READ_PHONE_STATE, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, PERMISSION_CODE);

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

                    if (sms && phone && storage && storage2 && recording) {
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

        getData();
        return super.onOptionsItemSelected(item);
    }

    public void getData() {
        Toast.makeText(this, "check", Toast.LENGTH_SHORT).show();

        Cursor cr = db.getUnsyncedCalls();

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

            uploadMultipart(0);
            //Toast.makeText(this, call.get(0).getCALL_NUMBER(), Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadMultipart(int pos) {
        //getting name for the image
        final Modal_Call m = call.get(pos);

        //Uploading code
        try {
            String uploadId = UUID.randomUUID().toString();

            //Creating a multi part request
            new MultipartUploadRequest(this, uploadId, UPLOAD_URL)
                    .addFileToUpload( m.getCALL_AUDIO(),"audio")
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
                    .startUpload();

            //Starting the upload


        } catch (Exception exc) {
            Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
