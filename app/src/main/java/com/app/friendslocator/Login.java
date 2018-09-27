package com.app.friendslocator;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.friendslocator.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    EditText edt_phoneNumber, edt_userName;
    Button btn_register;
    UserData userData;
    Utility utility;
    FirebaseAuth mAuth;
    DatabaseReference mDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userData = new UserData(this);
        utility = new Utility(this);

        mAuth = FirebaseAuth.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("users");

        edt_phoneNumber = (EditText) findViewById(R.id.edt_phoneNumber);
        edt_userName = (EditText) findViewById(R.id.edt_userName);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

    }


    // save phone number in  shared pref .
    public void registerUser() {
        if (utility.isNetworkAvailable()) {
            String phoneNumber = edt_phoneNumber.getText().toString().trim();
            String userName = edt_userName.getText().toString().trim();
            if (!TextUtils.isEmpty(phoneNumber) && !TextUtils.isEmpty(userName)) {
                userData.setPhoneNumber(phoneNumber);
                userData.setUserName(userName);

                signInAnonymously();
                mDataBase.child(phoneNumber).child("request").setValue("" + userData.getCurrentDate());
                mDataBase.child(phoneNumber).child("location").setValue("");  // null is initial value
                //  mDataBase.child(phoneNumber).child("trackers").setValue("");  // people who tracks me
                //  mDataBase.child(phoneNumber).child("tracking").setValue("");  // people i track
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }

        } else {
            Toast.makeText(Login.this, "Check your network!", Toast.LENGTH_SHORT).show();
        }

    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this, "Authenticated.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
