package com.example.smartparking1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    Button button;
    TextView button2;
    FirebaseAuth mAuth;
    EditText emailText, passwordText;

    CheckBox rememberMeCheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // User is already logged in, navigate to the next screen
            startActivity(new Intent(MainActivity.this, Secondpage.class));
            finish(); // Optional: Close the login activity so the user cannot go back to it
        } else {
            // User is not logged in, show the login screen
            setContentView(R.layout.activity_main);
            // Rest of your login screen initialization logic
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        button = findViewById(R.id.loginButton);
        button2 = findViewById(R.id.signupText);
        emailText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.rememberMe);
//        emailText = findViewById(R.id.editTextTextPersonName2);
//        passwordText = findViewById(R.id.editTextTextPassword2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
                //Intent intent = new Intent(MainActivity.this,Secondpage.class);
                //startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, Registration.class);
                startActivity(intent);
            }
        });
        boolean rememberMeChecked = sharedPreferences.getBoolean("rememberMeChecked", false);
        rememberMeCheckBox.setChecked(rememberMeChecked);

        rememberMeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("rememberMeChecked", isChecked);
                editor.apply();
            }
        });
    }
    private void loginUser(){
        String email_, password;
        email_ = emailText.getText().toString();
        password = passwordText.getText().toString();
        if (TextUtils.isEmpty(email_)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        Log.d("email", email_);
        Log.d("password", password);
        //Toast.makeText(getApplicationContext(), email_, Toast.LENGTH_LONG);
        mAuth.signInWithEmailAndPassword(email_, password)
                .addOnCompleteListener(
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task)
                            {
                                if (task.isSuccessful()) {
//                                    // i am storing the current logged in user's email id locally here
//                                    User currentUser=new User(email_);
//                                    Log.d("Firestore", "Email Stored as : " + currentUser.getEmail());
                                    Toast.makeText(getApplicationContext(),
                                                    "Login successful!!",
                                                    Toast.LENGTH_LONG)
                                            .show();

                                    // if sign-in is successful
                                    // intent to home activity
                                    SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLoggedIn", true);
                                    editor.apply();
                                    Intent intent
                                            = new Intent(MainActivity.this,
                                            Secondpage.class);
                                    startActivity(intent);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("login", e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}