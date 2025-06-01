package com.littlebits.sensorapp.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.littlebits.sensorapp.manager.PersonalDetailsManager;

public class SOSDialer {

    public static void dialSOS(Context context) {
        PersonalDetailsManager manager = new PersonalDetailsManager(context);
        String sosNumber = manager.getSavedData("sos_contact");

        if (sosNumber == null || sosNumber.isEmpty()) {
            Toast.makeText(context, "No SOS contact number saved", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + sosNumber));
        context.startActivity(intent);
    }
}
