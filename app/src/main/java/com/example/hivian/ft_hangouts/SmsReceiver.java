package com.example.hivian.ft_hangouts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by hivian on 2/1/17.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String phone;
    private String message;
    StringBuilder sb;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            DBHandler db = new DBHandler(context);

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                // get sms objects
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus.length == 0) {
                    return;
                }
                // large message might be broken into many
                SmsMessage[] messages = new SmsMessage[pdus.length];
                sb = new StringBuilder();
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    sb.append(messages[i].getMessageBody());
                }


                phone = messages[0].getOriginatingAddress();
                message = sb.toString();

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                Log.d("DEBUG", "WORKED");
                // prevent any other broadcast receivers from receiving broadcast
                // abortBroadcast();
            }
            db.close();
        }
    }
}
