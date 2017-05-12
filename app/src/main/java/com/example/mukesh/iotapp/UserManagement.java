package com.example.mukesh.iotapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagement extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef,activeRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG="UserMngt";
    private ArrayList<String> uids =new ArrayList<>();
    private ArrayList<String> names=new ArrayList<>();
    private ListView listView;
    ArrayAdapter<String> arrayAdapter;
    private EditText email_popup,username_popup,pwd_popup;
    boolean isListenerAdded = false;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView =(ListView) findViewById(R.id.list_view);
        arrayAdapter=new MyListAdapter(this,R.layout.content_user_management,names);
        listView.setAdapter(arrayAdapter);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        mAuth = FirebaseAuth.getInstance();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    uids.add(child.getKey());
                }
                for(int i=0;i<uids.size();i++){
                    Log.d(TAG,"asdasd "+uids.get(i));
                        addUserToArray(uids.get(i));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog=new Dialog(UserManagement.this);
                dialog.setTitle("set Timer for light1");
                dialog.setContentView(R.layout.add_user);

                email_popup=(EditText) dialog.findViewById(R.id.et_emailId);
                username_popup=(EditText) dialog.findViewById(R.id.et_username);
                pwd_popup=(EditText) dialog.findViewById(R.id.pwd);
                Button cancel= (Button) dialog.findViewById(R.id.cancel_button);
                Button register= (Button) dialog.findViewById(R.id.register_button);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String username_pop=username_popup.getText().toString();
                        final String emailid=email_popup.getText().toString();
                        String password=pwd_popup.getText().toString();
                        mAuth.createUserWithEmailAndPassword(emailid, password)
                                .addOnCompleteListener(UserManagement.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful()+"uid"+mAuth.getCurrentUser().getUid());
                                        final String RegisteredUser=mAuth.getCurrentUser().getUid();
                                        mAuth.signOut();

                                        mAuth.signInWithEmailAndPassword("a@a.com","123456").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                database.getReference("active").child(RegisteredUser).setValue(true);
                                                Log.d(TAG, "createUserWithEmail:onComplete inside the admin:" + task.isSuccessful()+"uid"+mAuth.getCurrentUser().getUid());
                                                String key = database.getReference("users").push().getKey();

                                                Map<String, Object> childUpdates = new HashMap<>();
                                                childUpdates.put( "email", emailid);
                                                childUpdates.put( "name", username_pop);
                                                childUpdates.put( "uid", RegisteredUser);

                                                // other properties here
                                                database.getReference("users/"+RegisteredUser).updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                        if (databaseError == null) {
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                        if (!task.isSuccessful()) {

                                        }
                                        // ...
                                    }
                                });
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
      }

    public void addUserToArray(String uid){

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");


childEventListener=new ChildEventListener() {
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String username=dataSnapshot.child("name").getValue(String.class);
        String emailid=dataSnapshot.child("email").getValue(String.class);
        String uid=dataSnapshot.child("uid").getValue(String.class);
        String user_email=username+":"+emailid+":"+uid;
        Log.d(TAG,"from adduser value event listener "+user_email+" from"+dataSnapshot.getKey());
        names.add(user_email);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
};
        if (!isListenerAdded) {
            myRef.addChildEventListener(childEventListener);
            isListenerAdded = true;
        }
}

    @Override
    public void onPause() {
        super.onPause();
        // Always call the superclass method first
        if (isListenerAdded) {
            myRef.removeEventListener(childEventListener);
        }
    }

    private class MyListAdapter extends ArrayAdapter<String>{
        private int layout;
        public MyListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
            layout=resource;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainViewHolder = null;
            if(convertView==null)
            {
                LayoutInflater inflater=LayoutInflater.from(getContext());
                convertView=inflater.inflate(layout,parent,false);
                final ViewHolder viewHolder=new ViewHolder();
                viewHolder.textView_name=(TextView) convertView.findViewById(R.id.reView_tv_name);
                viewHolder.textView_email =(TextView) convertView.findViewById(R.id.reView_tv_email);
                viewHolder.switch_status=(Switch) convertView.findViewById(R.id.switch1);
                String email_user=getItem(position);

                viewHolder.textView_name.setText(getItem(position).split(":")[0]);
                viewHolder.textView_email.setText(getItem(position).split(":")[1]);
                viewHolder.switch_status.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activeRef = database.getReference("active/"+getItem(position).split(":")[2]);
                        if (viewHolder.switch_status.isChecked()){
                            viewHolder.switch_status.setText("Enabled");
                            activeRef.setValue(true);}
                        else{
                            viewHolder.switch_status.setText("Disabled");
                            activeRef.setValue(false);}
                    }
                });


                Log.d(TAG,"finaljnbfjhgfhgfhgf "+email_user.split(":")[2]+" and email"+email_user.split(":")[1]);
                convertView.setTag(viewHolder);
            }

            return convertView;
        }
    }

    public class ViewHolder{
        TextView textView_name;
        TextView textView_email;
        Button button_remove;
        Switch switch_status;
    }

}
