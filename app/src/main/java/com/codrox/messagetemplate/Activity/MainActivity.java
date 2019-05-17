package com.codrox.messagetemplate.Activity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codrox.messagetemplate.DataBase;
import com.codrox.messagetemplate.Prefrence;
import com.codrox.messagetemplate.R;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.SEND_SMS;

public class MainActivity extends AppCompatActivity {

    DataBase db;
    Button b1, b2;

    public final int PERMISSION_CODE = 12345;

    Prefrence sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS, READ_PHONE_STATE}, PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
        }
    }
}
