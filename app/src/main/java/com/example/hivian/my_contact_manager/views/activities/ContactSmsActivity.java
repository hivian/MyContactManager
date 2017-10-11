package com.example.hivian.my_contact_manager.views.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hivian.my_contact_manager.R;
import com.example.hivian.my_contact_manager.receivers.SmsReceiver;
import com.example.hivian.my_contact_manager.recyclers.sms.SmsData;
import com.example.hivian.my_contact_manager.recyclers.sms.SmsListRecycler;
import com.example.hivian.my_contact_manager.utilities.Utility;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.Sms;
import com.example.hivian.my_contact_manager.models.db.DBHandler;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ContactSmsActivity extends AppCompatActivity {

    private static final String SMS_SENT = "SMS_SENT";
    private static final String SMS_DELIVERED = "SMS_DELIVERED";
    public static final String SMS_RECEIVED = "SMS_RECEIVED";
    private static Bundle extras;
    private EditText smsBody;
    public RecyclerView recyclerView;
    private List<SmsData> allData;
    private Contact contact;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_sms);
        extras = getIntent().getExtras();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(extras.getString("name"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Utility.changeStatusBarColor(this);

        smsBody = (EditText) findViewById(R.id.sms_body);
        db = DBHandler.getInstance(this);
        contact = db.getContactByName(extras.getString("name"));
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_sms);
        allData = new ArrayList<>();

        List<Sms> allSms = db.getAllSmsFromContact(contact.getId());
        for (Sms sms : allSms) {
            allData.add(new SmsData(sms.getHeader(), sms.getContent(), sms.getType()));
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_sms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new SmsListRecycler(allData));

        final EditText smsBody = (EditText) findViewById(R.id.sms_body);
        final ImageButton smsSender = (ImageButton) findViewById(R.id.sms_sender);
        smsBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    smsSender.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorDark),
                            PorterDuff.Mode.SRC_ATOP);
                } else {
                    smsSender.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                            android.R.color.darker_gray),
                            PorterDuff.Mode.SRC_ATOP);
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
                Utility.hideKeyboard(this);
                finish();
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
        registerReceiver(refreshReceiver, new IntentFilter(SMS_RECEIVED));
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        unregisterReceiver(sendBroadcastReceiver);
        unregisterReceiver(deliveryBroadcastReceiver);
        unregisterReceiver(refreshReceiver);
    }

    private BroadcastReceiver sendBroadcastReceiver = new BroadcastReceiver() {
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

    private BroadcastReceiver deliveryBroadcastReceiver = new BroadcastReceiver() {
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

    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent i) {
            String header = i.getStringExtra(SmsReceiver.SMS_HEADER);
            String content = i.getStringExtra(SmsReceiver.SMS_CONTENT);
            int type = i.getIntExtra(SmsReceiver.SMS_TYPE, Sms.RECEIVED);

            refreshSmsData(new Sms(header, content, contact.getId(), type));
        }
    };

    private void refreshSmsData(Sms sms) {
        allData.add(new SmsData(sms.getHeader(), sms.getContent(), sms.getType()));
        recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
    }

    public void sendSms(View view) {
        if (smsBody.getText().toString().trim().length() == 0)
            return ;
        String phone = extras.getString("phone");

        PendingIntent sentPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
        PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                DateFormat.MEDIUM, Locale.getDefault());
        Sms sms = new Sms(dateFormat.format(new Date()), smsBody.getText().toString(),
                contact.getId(), Sms.SENT);
        db.addSms(sms);

        refreshSmsData(sms);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, smsBody.getText().toString(),
                sentPendingIntent, deliveredPendingIntent);
        smsBody.setText("");
    }

}
