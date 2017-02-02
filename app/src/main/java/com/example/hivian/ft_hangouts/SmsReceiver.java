package com.example.hivian.ft_hangouts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hivian on 2/1/17.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private ArrayList<List<String>> allData;
    List<SmsContent> allSms;
    Contact contact;
    private String phone;
    private String message;
    StringBuilder sb;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            DBHandler db = new DBHandler(context);

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
                    if (contact == null)
                        return ;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                db.addSms(new SmsContent(sdf.format(new Date()), message, contact.getId(), SmsContent.RECEIVED));

                allData = new ArrayList<>();
                allSms = db.getAllSmsFromContact(contact.getId());
                for (SmsContent sms : allSms) {
                    ArrayList<String> elem = new ArrayList<>();
                    elem.add(sms.getHeader());
                    elem.add(sms.getContent());
                    elem.add(sms.getType().toString());
                    allData.add(elem);
                }
                ContactSms.setAdapter(new CustomSmsAdapter(context, allData));
                ContactSms.listView.setAdapter(ContactSms.getAdapter());
            }
            db.close();
        }
    }
}
