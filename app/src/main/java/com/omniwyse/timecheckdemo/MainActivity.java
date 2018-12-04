package com.omniwyse.timecheckdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String time = "";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private String phoneNo = "";
    private String message = "";


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.updateBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                finish();
            }
        });

        findViewById(R.id.sendBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSIntent();
            }
        });

        getCurrentTime();
        gpsTime();
        networkTime();
        phoneNo = "8600585624";
        message = "from " + getString(R.string.app_name) + " " + new Date().toString();

         sendSMSWithReq(phoneNo, message);
       // sendSMS(phoneNo, message);
    }


    private void getCurrentTime() {
        Calendar c = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ssZ");
        // df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String formattedDate = df.format(c.getTime());
        TextView txtView = findViewById(R.id.currentTimeTv);
        txtView.setText("Current Date and Time : " + formattedDate);
    }


    private void gpsTime() {
        android.location.LocationManager locationManager = (android.location.LocationManager)
                this.getSystemService(android.content.Context.LOCATION_SERVICE);
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
            public void onLocationChanged(android.location.Location location) {
                time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ssZ").format(location.getTime());
                TextView txtView = findViewById(R.id.gpsTimeTv);
                txtView.setText("GPS Date and Time : " + time);
            }

            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (android.support.v4.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.util.Log.d("Location", "Incorrect 'uses-permission', requires 'ACCESS_FINE_LOCATION'");
            return;
        }

        //   locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000, 0, locationListener);


    }


    private void networkTime() {
        android.location.LocationManager locationManager = (android.location.LocationManager)
                this.getSystemService(android.content.Context.LOCATION_SERVICE);
        android.location.LocationListener locationListener = new android.location.LocationListener() {
            @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
            public void onLocationChanged(android.location.Location location) {
                time = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ssZ").format(location.getTime());
                TextView txtView = findViewById(R.id.networkTimeTv);
                txtView.setText("Network Date and Time : " + time);
            }

            public void onStatusChanged(String provider, int status, android.os.Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        if (android.support.v4.content.ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            android.util.Log.d("Location", "Incorrect 'uses-permission', requires 'ACCESS_FINE_LOCATION'");
            return;
        }

        locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);


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
                        new String[]{Manifest.permission.SEND_SMS},
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


    private void sendSMSIntent() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==200){
            Log.d("TAGGG", "dsfdfgasdgsdg");
        }
    }

}
