package com.narmware.samista.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


public class SMSReciver extends BroadcastReceiver {
@Override
public void onReceive(Context context, Intent intent) {

final Bundle bundle = intent.getExtras();
    try {
        if (bundle != null) {
        final Object[] pdusObj = (Object[]) bundle.get("pdus");

        for (int i = 0; i < pdusObj.length; i++) {
        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
        String senderNum = phoneNumber ;
        String message = currentMessage .getDisplayMessageBody();
            message = message.replaceAll("[^\\d]", "");

        try {
            //if (senderNum.contains("LSOFT")) {

                //SignInActivity.getOtpFromSms(message);
                Log.e("Recieved msgs",message);
           //}
    } catch(Exception e){}
    }
    }
    } catch (Exception e) {}
    }
}