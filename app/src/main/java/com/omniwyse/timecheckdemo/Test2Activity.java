package com.omniwyse.timecheckdemo;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class Test2Activity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 200;
    ContentResolver content;
    ContentResolver contentResolver;
    private String phoneNo;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        contentResolver = this.getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms/sent"),
                true,
                new mObserver(new Handler()));

        // setContentView(R.layout.activity_main);

      //  sendSMSIntent();

        String phoneNo = "8600585624";
        String message = "from " + getString(R.string.app_name) + " " + new Date().toString();
        sendSMSWithReq(phoneNo,message);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    class mObserver extends ContentObserver {
        public mObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.i("Status","onChange");
            Uri uriSMS = Uri.parse("content://sms/sent");
            Cursor cur = getContentResolver().query(uriSMS, null, null, null, null);
            //Log.i("SMS", "Columns: " + cur.getColumnNames());
            cur.moveToNext();
            String smsText = cur.getString(cur.getColumnIndex("body"));
            Log.i("SMS", "SMS Lenght: " + smsText.length());
        }
    }

    private void sendSMSIntent() {
        String phoneNo = "8600585624";
        String message = "from " + getString(R.string.app_name) + " " + new Date().toString();
        String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra("address", phoneNo);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        if (defaultSmsPackageName != null)
        {
            sendIntent.setPackage(defaultSmsPackageName);
        }
        startActivityForResult(sendIntent,200);

    }

    protected void sendSMSWithReq(String phoneNo1, String message1) {
        phoneNo = phoneNo1;
        message = message1;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                sendSMS(phoneNo, message);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            sendSMS(phoneNo, message);

        }
    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS(phoneNo, message);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Permission Not granted.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }



}
