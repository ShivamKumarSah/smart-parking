package com.example.smartparking1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private TextView usernameTextView, emailTextView, phoneTextView, carNoTextView, carNameTextView, licenseTextView, currentPlan, renewDateView, parksLeft, changePlanBtn;
    private CardView updateProfile;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId, formattedRenewDateTime;
    private LinearLayout renewdateSection, pHoursLeftSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        usernameTextView = findViewById(R.id.user_name);
        emailTextView = findViewById(R.id.mail);
        phoneTextView = findViewById(R.id.phone);
        updateProfile = findViewById(R.id.edit_profile);
        carNoTextView = findViewById(R.id.car_no);
        carNameTextView = findViewById(R.id.car_name);
        licenseTextView = findViewById(R.id.license);
        currentPlan = findViewById(R.id.currentPlan);
        renewDateView = findViewById(R.id.renewDate);
        parksLeft = findViewById(R.id.parksLeft);
        changePlanBtn = findViewById(R.id.changePlanBtn);
        renewdateSection = findViewById(R.id.renewdateSection);
        pHoursLeftSection = findViewById(R.id.pHoursLeftSection);

        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.profile_bkg);

        RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, relativeLayout.getHeight());
        imageView.setLayoutParams(layoutParams);

        relativeLayout.addView(imageView);


        // get firestore data
            mAuth = FirebaseAuth.getInstance();
            UserId = mAuth.getCurrentUser().getUid();
            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("users").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
                String user_name = documentSnapshot.getString("name");
                String email  = documentSnapshot.getString("email");
                String phone  = documentSnapshot.getString("phone");
                String car_no  = documentSnapshot.getString("carNo");
                String car_name  = documentSnapshot.getString("carName");
                String license  = documentSnapshot.getString("license");

                usernameTextView.setText(user_name);
                emailTextView.setText(email);
                phoneTextView.setText(phone);
                carNoTextView.setText(car_no);
                carNameTextView.setText(car_name);
                licenseTextView.setText(license);
            });

            mFirestore.collection("users").document(UserId).collection("membership").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
                String plan = documentSnapshot.getString("plan");
                String inputDateTime  = documentSnapshot.getString("renewDate");
                String parks  = documentSnapshot.getString("parks");

                // date calculation dd-MM-yyyy hh:mm am/pm format
                try {
                    SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = sdfInput.parse(inputDateTime);
                    SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                    formattedRenewDateTime = sdfOutput.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currentPlan.setText(plan);

                if(!plan.equals("Free"))
                {
                    renewDateView.setText(formattedRenewDateTime);
                    parksLeft.setText(parks);
                }
                else{
                    renewdateSection.setVisibility(View.GONE);
                    pHoursLeftSection.setVisibility(View.GONE);
                }
            });

        updateProfile.setOnClickListener(v -> {
            Intent intent=new Intent(ProfileActivity.this, UpdateActivity.class);
            startActivity(intent);
        });

        changePlanBtn.setOnClickListener(v -> {
            Intent intent=new Intent(ProfileActivity.this, Subscription.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        // Navigate back to the SecondActivity
        Intent intent = new Intent(ProfileActivity.this, Secondpage.class);
        startActivity(intent);
        finish(); // Optional: finish the current activity to remove it from the stack
    }

}
