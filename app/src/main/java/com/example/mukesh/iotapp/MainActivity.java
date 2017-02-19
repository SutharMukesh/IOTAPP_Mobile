package com.example.mukesh.iotapp;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    public static String TAG="MAINACT";
    private String name,email;
    private TextView txtname;
//Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//App basic initialisation
        txtname= (TextView) findViewById(R.id.name);
        final ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

//firebase initialisation
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("light");
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "inside Authstatechanged");
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Toast.makeText(MainActivity.this,"u dont exist",Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(MainActivity.this,Login.class));


                }
            }
        };
//displaying the username on mainActivity
        name = user.getDisplayName();
        email = user.getEmail();
        txtname.setText(name);
        txtname.setTextSize(20);
        Log.d(TAG,"name "+name+" Email "+email);

//Togglebutton initialized and set oncheck listener
        toggleButton.setText("Connecting...");
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



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void onn(View v){
        myRef.setValue(true);

    }

    public void off(View v){
        myRef.setValue(false);

    }
    public void signout(View v)
    {
        FirebaseAuth.getInstance().signOut();

    }
}
