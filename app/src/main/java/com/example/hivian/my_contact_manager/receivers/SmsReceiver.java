package com.example.hivian.my_contact_manager.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.example.hivian.my_contact_manager.recyclers.sms.SmsAdapter;
import com.example.hivian.my_contact_manager.views.activities.ContactSmsActivity;
import com.example.hivian.my_contact_manager.models.Contact;
import com.example.hivian.my_contact_manager.models.Sms;
import com.example.hivian.my_contact_manager.models.db.DBHandler;
import com.example.hivian.my_contact_manager.services.SmsNotificationService;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by hivian on 2/1/17.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private ArrayList<List<String>> allData;
    List<Sms> allSms;
    Contact contact;
    private String phone;
    private String message;
    StringBuilder sb;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            DBHandler db = DBHandler.getInstance(context);

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                SmsMessage[] messages = new SmsMessage[pdus.length];
                sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }

                message = sb.toString();
                phone = messages[0].getOriginatingAddress();
                contact = db.getContactByPhone(phone);
                if (contact == null) {
                    phone = messages[0].getOriginatingAddress().replace("+33", "0");
                    contact = db.getContactByPhone(phone);
                    if (contact == null) {
                        db.addContact(new Contact(null, phone.replaceAll("\\s+",""),
                                phone.replaceAll("\\s+",""), "", ""));
                    }
                }

                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,
                        DateFormat.MEDIUM, Locale.getDefault());
                contact = db.getContactByPhone(phone);
                db.addSms(new Sms(df.format(new Date()), message, contact.getId(), Sms.RECEIVED));

                allData = new ArrayList<>();
                allSms = db.getAllSmsFromContact(contact.getId());
                for (Sms sms : allSms) {
                    ArrayList<String> elem = new ArrayList<>();
                    elem.add(sms.getHeader());
                    elem.add(sms.getContent());
                    elem.add(sms.getType().toString());
                    allData.add(elem);
                }
                if (ContactSmsActivity.getAdapter() != null) {
                    ContactSmsActivity.setAdapter(new SmsAdapter(context, allData));
                    ContactSmsActivity.listView.setAdapter(ContactSmsActivity.getAdapter());
                }

                Intent i = new Intent(context, SmsNotificationService.class);
                i.putExtra("contentTitle", contact.getName());
                i.putExtra("contentMessage", message);
                context.startService(i);
            }
        }
    }
}
