package com.example.mukesh.iotapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import android.view.View;
import android.widget.EditText;

import android.widget.Toast;

import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private String TAG="EmailLogin";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText passworded,emailed;
    private ProgressDialog progressDialog;
    private String email,password;
    FirebaseUser user;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailed= (EditText) findViewById(R.id.editText);
        passworded= (EditText) findViewById(R.id.editText2);
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

//Listener for authentication
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "inside Authstatechanged-login");
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in-login:" + user.getUid());
                    startActivityForResult(new Intent(Login.this,MainActivity.class),RC_SIGN_IN);
                } else {
                    // User is signed outa
                    Log.d(TAG, "onAuthStateChanged:signed_out-login");
                }
            }
        };

        ConnectivityManager connectivityManager= (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){

        }else{
            Toast.makeText(Login.this,"Please connect to Internet",Toast.LENGTH_SHORT).show();
        }
    }//oncreate ends

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            // Successfully signed in
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,"signedIn",Toast.LENGTH_LONG).show();
            } else {
                // Sign in failed
                if (resultCode == RESULT_CANCELED) {
                    // User pressed back button
                    Log.d(TAG,"activityresult");
                    finish();
                }
                if (resultCode== ResultCodes.RESULT_NO_NETWORK) {
                    Toast.makeText(this,"no Internet",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

   public void signin(final View v){

       Log.d(TAG,"Signing in");
       email = emailed.getText().toString();
       password = passworded.getText().toString();
       /*if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)){
            Toast.makeText(Login.this,"cannot leave email or password blank",Toast.LENGTH_SHORT).show();
       }
       */
       if (!validateForm()) {
           return;
       }


        progressDialog.setMessage("signing in");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG,"inside signinWithEmailadnPasswd");
                progressDialog.dismiss();
                if(!task.isComplete()){
                    Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


}

    private boolean validateForm() {
        boolean valid = true;

        String email = emailed.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailed.setError("Required.");
            valid = false;
        } else {
            emailed.setError(null);
        }

        String password = passworded.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passworded.setError("Required.");
            valid = false;
        } else {
            passworded.setError(null);
        }

        return valid;
    }
}
