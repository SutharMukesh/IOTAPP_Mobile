package com.example.mukesh.iotapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;

public class EmailPasswordActivity extends AppCompatActivity {

    String TAG="EmailLogin";

    EditText emailed= (EditText) findViewById(R.id.editText);
    EditText passworded= (EditText) findViewById(R.id.editText2);
    String email =emailed.getText().toString();
    String password=passworded.getText().toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);
    }
}
