package com.example.mukesh.iotapp;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class FirebaseInstanceServiceId extends FirebaseInstanceIdService {
    private static final String REG_TOKEN="Reg_token";
    @Override
    public void onTokenRefresh() {
        String recent_token= FirebaseInstanceId.getInstance().getToken();

        Log.d(REG_TOKEN,recent_token);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("light");
        reference.child("token").setValue(recent_token);
    }

}
