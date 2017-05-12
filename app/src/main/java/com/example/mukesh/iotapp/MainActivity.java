package com.example.mukesh.iotapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static String TAG = "MAINACT";
    private String name, email,L1="Light",log="log";
    private TextView txtname,nav_name_tv,nav_email_tv,mtimer,mtimertext;
    private Intent intent;
    private ToggleButton toggleButton;
    private int startTime,endTime,usageTime,hours,mins,secs,millisecs;
    private DrawerLayout mdrawer;
    private ActionBarDrawerToggle mToggle;
    private Boolean isChecked;
    private Button mtimerButton1,mtimerButton2,mtimerAbort,mcontrol;
    private SimpleDateFormat format2,format1;
    //Firebase variables
    private FirebaseDatabase database;
    private DatabaseReference myRef,mlog,mUsage,mPostReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Menu menu;
    private NavigationView navigationView;
    private CardView cardView;
    private CountDownTimer countDownTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        database.getReference("active/"+FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean active=dataSnapshot.getValue(Boolean.class);
                if(active){
                    Toast.makeText(MainActivity.this,"your account is Enabled",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this,"your account is disabled",Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(MainActivity.this,ContactAdmin.class), RC_SIGN_IN);
            }
        });
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        menu= navigationView.getMenu();
//App basic initialisation
        //popup window (Related to Timer)
        mtimerButton1= (Button) findViewById(R.id.timer_button);
        mtimerButton2= (Button) findViewById(R.id.timer_button2);
        mcontrol=(Button) findViewById(R.id.controlButton);
        mtimerAbort=(Button) findViewById(R.id.abort);
        mtimertext=(TextView) findViewById(R.id.textView);
        mtimerAbort.setVisibility(View.INVISIBLE);
        mtimertext.setVisibility(View.INVISIBLE);
        cardView= (CardView)findViewById(R.id.timer_cardView);
        cardView.setVisibility(View.INVISIBLE);
        DisplayMetrics displayMetrics=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mtimerButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog=new Dialog(MainActivity.this);
                dialog.setTitle("set Timer for light1");
                dialog.setContentView(R.layout.popup1);

                final NumberPicker np1= (NumberPicker) dialog.findViewById(R.id.np_hours);
                final NumberPicker np2= (NumberPicker) dialog.findViewById(R.id.np_mins);
                final NumberPicker np3= (NumberPicker) dialog.findViewById(R.id.np_secs);
                Button on= (Button) dialog.findViewById(R.id.timer_ON);
                Button off= (Button) dialog.findViewById(R.id.timer_OFF);
                Button cancel= (Button) dialog.findViewById(R.id.timer_cancel);

                np1.setMaxValue(24);
                np1.setMinValue(0);
                np2.setMaxValue(60);
                np2.setMinValue(0);
                np3.setMaxValue(60);
                np3.setMinValue(0);
                np1.setWrapSelectorWheel(false);

                np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        hours=newVal;
                        millisecs=(hours*3600+mins*60+secs)*1000;
                        Log.d(TAG,"inside hrs "+millisecs);
                    }
                });
                np2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        mins=newVal;
                        millisecs=(hours*3600+mins*60+secs)*1000;
                        Log.d(TAG,"inside mins "+millisecs);
                    }
                });
                np3.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                        secs=newVal;
                        millisecs=(hours*3600+mins*60+secs)*1000;
                        Log.d(TAG,"inside sec "+millisecs);
                    }
                });

                on.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        mtimertext.setText("Time Remaining:");
                        cardView.setVisibility(View.VISIBLE);
                        mtimerAbort.setVisibility(View.VISIBLE);
                        mtimertext.setVisibility(View.VISIBLE);

                       countDownTimer = new CountDownTimer(millisecs, 1000) {
                            public void onTick(long millisUntilFinished) {
                                mtimer.setText("" + millisUntilFinished / 1000);
                            }
                            public void onFinish() {
                                mtimertext.setText("done!");
                                mtimer.setText("");
                                myRef.setValue("true:"+name);
                            }
                        }.start();
                        dialog.dismiss();
                    }
                });

                off.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        mtimertext.setText("Time Remaining:");
                        cardView.setVisibility(View.VISIBLE);
                        mtimerAbort.setVisibility(View.VISIBLE);
                        mtimertext.setVisibility(View.VISIBLE);

                        countDownTimer = new CountDownTimer(millisecs, 1000) {
                            public void onTick(long millisUntilFinished) {
                                mtimer.setText("" + millisUntilFinished / 1000);
                            }
                            public void onFinish() {
                                mtimertext.setText("done!");
                                mtimer.setText("");
                                myRef.setValue("false:"+name);                            }
                        }.start();
                        dialog.dismiss();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
        mtimerAbort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                cardView.setVisibility(View.INVISIBLE);
            }
        });
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

        mtimer= (TextView) findViewById(R.id.time_tv);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        intent = new Intent(this, Login.class);
//firebase initialisation
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("light/status/");
        mlog=database.getReference("log");
        mUsage=database.getReference("usage");
        mAuth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().subscribeToTopic("Power_Notifications");
        FirebaseMessaging.getInstance().subscribeToTopic("rpi_status");

        Log.d(TAG, "inside main oncreate");
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        onSignInInitialize(firebaseUser);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "inside Authstatechanged");
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
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
            startActivity( new Intent(MainActivity.this,Reports.class));
        } else if (id == R.id.addremoveUsers) {
          startActivity( new Intent(MainActivity.this,UserManagement.class));

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

    private void onSignInInitialize(final FirebaseUser user) {
       String result= new SendSMS().SendSms();
        Log.d("result from Messaging",result);

        if((user.getUid()).equals("MMuN2cl3wDWcK1bCMbDnCZcdyD33"))
         menu.findItem(R.id.addremoveUsers).setVisible(true);
        else
            menu.findItem(R.id.addremoveUsers).setVisible(false);
        mPostReference=database.getReference("users/"+mAuth.getCurrentUser().getUid()+"/name");
//displaying the username on mainActivity
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                name= dataSnapshot.getValue(String.class);
              //  txtname.setText(name);
                nav_name_tv.setText(name);
                Log.d(TAG, "name " + name + " Email " + email);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mPostReference.addValueEventListener(postListener);
        email = user.getEmail();
        nav_email_tv.setText(email);
        format1 = new SimpleDateFormat("yyyy-MM-dd");
        format2= new SimpleDateFormat("dd-MMM-yyyy");
        mcontrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                if(mcontrol.getText()=="off"){
                    myRef.setValue(true + ":" + name );
                    mcontrol.setText("on");
                    isChecked=true;
                }
                else {
                    isChecked=false;
                    myRef.setValue(false + ":" + name );
                    mcontrol.setText("off");
                }
                mlog.child(name).child("light1").push().setValue(timestamp.toString().concat(" " + isChecked));
                String TS[] = timestamp.toString().split(" ");
                if (mcontrol.getText()=="on")
                    startTime = (int) timestamp.getTime();
                else if(mcontrol.getText()=="off")
                    endTime = (int) timestamp.getTime();
                usageTime = (endTime - startTime) / 1000;

                if (mcontrol.getText()=="off") {
                    try {
                        Date date = format1.parse(TS[0]);
                        Toast.makeText(MainActivity.this, " it is used for " + usageTime + "sec", Toast.LENGTH_LONG).show();
                        mUsage.child(L1).push().setValue(usageTime + " " + format2.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

// Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is from datasnapshot: " + value);
                value = value.split(":")[0];
                if(value.equals("true"))
                    mcontrol.setText("on");
                else
                    mcontrol.setText("off");
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
}
