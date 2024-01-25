package com.example.smartparking1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

class User {
    private String name;
    private String email;
    private String car_name;
    private String car_no;
    private String phone;
    private String license;

    public User(String name, String email, String car_name, String car_no, String phone, String license) {
        this.name = name;
        this.email = email;
        this.car_name = car_name;
        this.car_no = car_no;
        this.phone = phone;
        this.license = license;
    }

    public User() {}

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCarName() {
        return car_name;
    }

    public String getCarNo() {
        return car_no;
    }

    public String getPhone() {
        return phone;
    }

    public String getLicense() {
        return license;
    }

    private DocumentReference docRefM;
}

public class Registration extends AppCompatActivity {
    Button bt1 ;
    FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText name, email, car_name, car_no, phone, license, password;

    TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_registration);
        bt1 = findViewById(R.id.registerButton);
        name = findViewById(R.id.personName);
        email = findViewById(R.id.email);
        car_name = findViewById(R.id.carName);
        car_no = findViewById(R.id.carNumber);
        phone = findViewById(R.id.mobile);
        license = findViewById(R.id.dLicence);
        password = findViewById(R.id.password);
        bt1.setOnClickListener(view -> registerNewUser());

        login = findViewById(R.id.loginText);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(Registration.this, MainActivity.class);
            startActivity(intent);
        });
    }
    private void registerNewUser(){
        String email_reg, password_reg, name_reg, car_name_reg, car_no_reg, phone_reg, license_reg;
        email_reg = email.getText().toString();
        password_reg = password.getText().toString();
        name_reg = name.getText().toString();
        car_name_reg = car_name.getText().toString();
        car_no_reg = car_no.getText().toString();
        phone_reg = phone.getText().toString();
        license_reg = license.getText().toString();
        if (TextUtils.isEmpty(name_reg)) {
            Toast.makeText(getApplicationContext(), "Please enter your Name!!", Toast.LENGTH_LONG);
            return;
        }
        if (TextUtils.isEmpty(car_name_reg)) {
            Toast.makeText(getApplicationContext(), "Please enter your Car Name!!", Toast.LENGTH_LONG);
            return;
        }
        if (TextUtils.isEmpty(car_no_reg)) {
            Toast.makeText(getApplicationContext(), "Please enter your Car Number!!", Toast.LENGTH_LONG);
            return;
        }
        if (TextUtils.isEmpty(phone_reg)) {
            Toast.makeText(getApplicationContext(), "Please enter your Phone Number!!", Toast.LENGTH_LONG);
            return;
        }
        if (TextUtils.isEmpty(license_reg)) {
            Toast.makeText(getApplicationContext(), "Please enter your License Number!!", Toast.LENGTH_LONG);
            return;
        }
        if (TextUtils.isEmpty(email_reg)) {
            Toast.makeText(getApplicationContext(),"Please enter email!!",Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password_reg)) {
            Toast.makeText(getApplicationContext(),"Please enter password!!",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth
                .createUserWithEmailAndPassword(email_reg, password_reg)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),"Registration successful!",Toast.LENGTH_LONG).show();
                        addDataToFirestore(name_reg, email_reg, car_name_reg, car_no_reg, phone_reg, license_reg);
                        Intent intent= new Intent(Registration.this,Secondpage.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> {
                    Log.d("Register", e.getMessage());
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private void addDataToFirestore(String name, String email, String car_name, String car_no, String phone, String license){
        CollectionReference dbCourses = db.collection("users");
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userDocRef = dbCourses.document(uid);
        DocumentReference bookingDocRef;
        DocumentReference membershipDocRef;

//        DocumentReference bookingDocRef = db.collection("users").document(uid).collection("bookings").document();
        bookingDocRef=db.collection("users").document(uid).collection("bookings").document(uid);
        membershipDocRef=db.collection("users").document(uid).collection("membership").document(uid);

        HashMap<String, Object> setupBookingSection = new HashMap<>();
        setupBookingSection.put("bookedAt", "");
        setupBookingSection.put("facilityId", "");
        setupBookingSection.put("floorNo", "");
        setupBookingSection.put("location", "");
        setupBookingSection.put("paid", "");
        setupBookingSection.put("parkedFrom", "");
        setupBookingSection.put("parkedTo", "");
        setupBookingSection.put("parkingName", "");
        setupBookingSection.put("slotNo", "");

        HashMap<String, Object> setupMembershipSection = new HashMap<>();
        setupMembershipSection.put("parkingCharges", "");
        setupMembershipSection.put("parks", "0");
        setupMembershipSection.put("plan", "Free");
        setupMembershipSection.put("planCharges", "");
        setupMembershipSection.put("rechargeDate", "");
        setupMembershipSection.put("renewDate", "");


        User u1 = new User(name, email, car_name, car_no, phone, license);

        userDocRef.set(u1)
                .addOnSuccessListener(aVoid -> {
                    bookingDocRef.set(setupBookingSection)
                            .addOnSuccessListener(s -> {
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Booking section can't be create", Toast.LENGTH_LONG).show();
                            });

                    membershipDocRef.set(setupMembershipSection)
                            .addOnSuccessListener(s -> {
                            }).addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Membership section can't be create", Toast.LENGTH_LONG).show();
                            });

                    Toast.makeText(getApplicationContext(), "Registered Sucessfully", Toast.LENGTH_LONG).show();
                    Log.d("data_add", "Successful");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Registeration Failed!", Toast.LENGTH_LONG).show();
                    Log.d("data_add", "Failure");
                });

    }
}