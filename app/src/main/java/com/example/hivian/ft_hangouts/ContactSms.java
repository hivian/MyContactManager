package com.example.hivian.ft_hangouts;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContactSms extends AppCompatActivity {

    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    private static final String SMS_SENT = "SMS_SENT";
    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    private static CustomSmsAdapter adapter;
    public static ListView listView;
    private static Bundle extras;
    private EditText smsBody;
    private ArrayList< List<String> > allData;
    private Contact contact;

    public static CustomSmsAdapter getAdapter() {
        return adapter;
    }

    public static void setAdapter(CustomSmsAdapter _adapter) {
       adapter = _adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sms);
        extras = getIntent().getExtras();
        getSupportActionBar().setTitle(Html.fromHtml("<font color='white'>" + extras.getString("name")  + "</font>"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        smsBody = (EditText) findViewById(R.id.sms_body);

        sendBroadcastReceiver = new BroadcastReceiver() {
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
        };

        deliveryBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), R.string.alert_sms_delivered_ok, Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), R.string.alert_sms_cancelled, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };


        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(SMS_DELIVERED));
        registerReceiver(sendBroadcastReceiver , new IntentFilter(SMS_SENT));

        DBHandler db = new DBHandler(this);
        contact = db.getContactByName(extras.getString("name"));
        List<SmsContent> allSms = db.getAllSmsFromContact(contact.getId());
        listView = (ListView) findViewById(R.id.listView_sms);
        allData = new ArrayList<>();
        for (SmsContent sms : allSms) {
            ArrayList<String> elem = new ArrayList<>();
            elem.add(sms.getHeader());
            elem.add(sms.getContent());
            elem.add(sms.getType().toString());
            allData.add(elem);
        }
        adapter = new CustomSmsAdapter(this, allData);
        listView.setAdapter(adapter);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, ContactInfo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("contact", contact);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(SMS_DELIVERED));
        registerReceiver(sendBroadcastReceiver , new IntentFilter(SMS_SENT));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            unregisterReceiver(sendBroadcastReceiver);
            unregisterReceiver(deliveryBroadcastReceiver);
        }
    }

    public void sendSms(View view) {
        DBHandler db = new DBHandler(this);

        if (smsBody.getText().toString().trim().length() == 0)
            return ;
        String phone = extras.getString("phone");

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SmsContent sms = new SmsContent(sdf.format(new Date()), smsBody.getText().toString(),
                contact.getId(), SmsContent.SENT);
        db.addSms(sms);
        List <String> elem = new ArrayList<>();
        elem.add(sms.getHeader());
        elem.add(sms.getContent());
        elem.add(sms.getType().toString());
        List<SmsContent> allSms = db.getAllSmsFromContact(contact.getId());

        allData = new ArrayList<>();
        for (SmsContent sms_data : allSms) {
            ArrayList<String> sms_elem = new ArrayList<>();
            sms_elem.add(sms_data.getHeader());
            sms_elem.add(sms_data.getContent());
            sms_elem.add(sms_data.getType().toString());
            allData.add(sms_elem);
        }
        adapter = new CustomSmsAdapter(this, allData);
        listView.setAdapter(adapter);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, smsBody.getText().toString(),
                sentPendingIntent, deliveredPendingIntent);
        smsBody.setText("");
        db.close();
    }
}
