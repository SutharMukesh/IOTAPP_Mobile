package com.example.mukesh.iotapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Arrays;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static String TAG = "MAINACT";
    private String name, email,L1="Light",log="log";
    private TextView txtname,nav_name_tv,nav_email_tv;
    private Intent intent;
    private ToggleButton toggleButton;
    private int startTime,endTime,usageTime;
    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mToggle;
//Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference myRef,mlog,mUsage;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//App basic initialisation
        //below all related to navigation bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        nav_name_tv= (TextView)header.findViewById(R.id.nav_textView);
        nav_email_tv= (TextView) header.findViewById(R.id.nav_email_tv);
        mdrawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToggle=new ActionBarDrawerToggle(this,mdrawer,toolbar,R.string.open,R.string.close);
        mdrawer.setDrawerListener(mToggle);
        mToggle.syncState();


        txtname = (TextView) findViewById(R.id.name);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        intent = new Intent(this, Login.class);
//firebase initialisation
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("light");
        mlog=database.getReference("log");
        mUsage=database.getReference("usage");
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG, "inside main oncreate");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        onSignInInitialize(firebaseUser);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
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
                }
            }
        };
    }
//again navigation bar related onBackPressed() go to mainActivity(i.e. drawerlayout)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
// this requires implementation of NavigationView.OnNavigationItemSelectedListener
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.control_appliance) {
            Toast.makeText(this, "appliance", Toast.LENGTH_LONG).show();
            // Handle the camera action
        } else if (id == R.id.reports) {
            Toast.makeText(this, "reports", Toast.LENGTH_LONG).show();
        } else if (id == R.id.addremoveUsers) {
            Toast.makeText(this, "addremove", Toast.LENGTH_LONG).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "signedIn", Toast.LENGTH_LONG).show();
            } else {
                // Sign in failed
                if (resultCode == RESULT_CANCELED) {
                    // User pressed back button
                    finish();
                }
                if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
                    Toast.makeText(this, "no Internet", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    private void onSignInInitialize(FirebaseUser user) {

//displaying the username on mainActivity
        name = user.getDisplayName();
        email = user.getEmail();
        txtname.setText(name);
        txtname.setTextSize(20);
        nav_email_tv.setText(email);
        nav_name_tv.setText(name);
        Log.d(TAG, "name " + name + " Email " + email);

//Togglebutton initialized and set oncheck listener
        toggleButton.setText("Connecting...");
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
              //  Bundle bundle=new Bundle();
               // bundle.putString("username",name);
               // bundle.putString("email",email);
             //   bundle.putString("device",myRef.toString());
               // mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                myRef.setValue(isChecked);
                mlog.child(name).child("light1").push().setValue(timestamp.toString().concat(" "+isChecked));
                String TS[]=timestamp.toString().split(" ");
                if(isChecked)
                    startTime= (int) timestamp.getTime();
                else
                    endTime= (int) timestamp.getTime();
                usageTime=(endTime-startTime)/1000;

                //mlog.child(name).child("timestamp").setValue([mlog.child(name).child("timestamp").getKey().length()+1] ,timestamp.toString());
                if(!isChecked) {
                    Toast.makeText(MainActivity.this, " it is used for " + usageTime + "sec", Toast.LENGTH_LONG).show();
                    mUsage.child(L1).push().setValue(usageTime+" "+TS[0]);
                }Log.d(TAG, " siad");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search)
            FirebaseAuth.getInstance().signOut();
        startActivityForResult(intent, RC_SIGN_IN);

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthListener == null)
            mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    public void signout(View v) {
        FirebaseAuth.getInstance().signOut();
        Log.w(TAG, "signout...");

    }
}
