package com.example.hivian.ft_hangouts;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactSms extends AppCompatActivity {

    private static final String SMS_SENT = "SMS_SENT";
    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    private static Bundle extras;
    private ListView listView;
    private CustomSmsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sms);
        extras = getIntent().getExtras();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + extras.getString("name")  + "</font>"));

        if (MainActivity.getPurple()) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.holo_purple)));
        } else {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }

        listView = (ListView) findViewById(R.id.listView_sms);

        //ArrayList<String> = new ArrayList<>();


        //adapter = new CustomSmsAdapter (this, allData);
       // listView.setAdapter(adapter);

        final EditText smsBody = (EditText) findViewById(R.id.sms_body);
        final ImageButton smsSender = (ImageButton) findViewById(R.id.sms_sender);

        smsBody.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    smsSender.setColorFilter(
                            getResources().getColor(R.color.colorDark), PorterDuff.Mode.SRC_ATOP);
                } else {
                    smsSender.getDrawable().setColorFilter(
                            getResources().getColor(android.R.color.darker_gray), PorterDuff.Mode.SRC_ATOP);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void sendSms(View view) {
        final EditText smsBody = (EditText) findViewById(R.id.sms_body);
        if (smsBody.getText().toString().trim().length() == 0)
            return ;
        String phone = extras.getString("phone");

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, R.string.alert_sms_sent_ok, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(context, R.string.alert_sms_generic_failure, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, R.string.alert_sms_no_service, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, R.string.alert_sms_no_pdu, Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(context, R.string.alert_sms_radio_off, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_SENT));

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), R.string.alert_sms_delivered_ok, Toast.LENGTH_SHORT).show();
                        smsBody.setText("");
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), R.string.alert_sms_cancelled, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SMS_DELIVERED));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, smsBody.getText().toString(),
                sentPendingIntent, deliveredPendingIntent);
    }

}
