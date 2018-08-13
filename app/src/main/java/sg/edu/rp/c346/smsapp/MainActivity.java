package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button btnSend;
    BroadcastReceiver br = new MessageReceiver();
    Button btnMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        etTo = findViewById(R.id.editTextTo);
        etContent = findViewById(R.id.editTextContent);
        btnSend = findViewById(R.id.buttonSend);
        btnMsg = findViewById(R.id.buttonMessage);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String to = etTo.getText().toString();
                String content = etContent.getText().toString();

                StringTokenizer st = new StringTokenizer(to, ";");
                while(st.hasMoreElements()){
                    String tempNo = (String)st.nextElement();
                    if(tempNo.length() > 0 && content.trim().length() > 0 ){
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(tempNo, null, content, null, null );

                    }
                }


                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
                etContent.setText("");
            }
        });

        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendSMS();
            }

        });



        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    private void sendSMS() {

        String to = etTo.getText().toString();
        String content = etContent.getText().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            //String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(this); // Need to change the build to API 19

            Uri sms_uri = Uri.parse("smsto:" + to);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            sendIntent.putExtra("sms_body", content);

            startActivity(sendIntent);

        }
        else // For early versions, do what worked for you before.
        {


            Uri sms_uri = Uri.parse("smsto:" + to);
            Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
            sms_intent.putExtra("sms_body", content);
            startActivity(sms_intent);
        }
    }
}
