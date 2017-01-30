package com.example.mukesh.iotapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
public static String TAG="MAINACT";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("light");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initiate a Switch
      //  final Switch simpleSwitch = (Switch) findViewById(R.id.switch1);
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
// check current state of a Switch (true or false).
        toggleButton.setText("button");
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             myRef.setValue(isChecked);
                toggleButton.setText("hel");
                Log.d(TAG," siad");
            }
        });

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Boolean value = dataSnapshot.getValue(Boolean.class);
                Log.d(TAG, "Value is: " + value);
                    toggleButton.setChecked(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public void onn(View v){
        myRef.setValue(true);

    }

    public void off(View v){
        myRef.setValue(false);

    }
}
