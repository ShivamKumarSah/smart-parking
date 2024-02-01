package com.example.smartparking1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Recharge extends AppCompatActivity {
    private Button changePlanBtn, rechargeBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private TextView currentPlan, renewDateView, parkingHoursLeft, planValidity, parkingHours, pricing, PlanName, pricingHolder,parkingHoursHolder,  planValidityHolder;
    private String UserId, formattedRenewDate, formattedRenewDateTime, plan;
    private CardView planCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);

        changePlanBtn=findViewById(R.id.changePlanBtn);
        rechargeBtn=findViewById(R.id.rechargeBtn);
        currentPlan=findViewById(R.id.currentPlan);
        renewDateView=findViewById(R.id.renewDate);
        parkingHoursLeft=findViewById(R.id.parkingHoursLeft);

        // card 2
        planCardView=findViewById(R.id.planCardView);
        PlanName=findViewById(R.id.PlanName);
        planValidity=findViewById(R.id.planValidity);
        parkingHours=findViewById(R.id.parkingHours);
        pricing=findViewById(R.id.pricing);
        planValidityHolder=findViewById(R.id.planValidityHolder);
        parkingHoursHolder=findViewById(R.id.parkingHoursHolder);
        pricingHolder=findViewById(R.id.pricingHolder);


        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("users").document(UserId).collection("membership").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
            plan = documentSnapshot.getString("plan");
            String renewDate  = documentSnapshot.getString("renewDate");
            String parks  = documentSnapshot.getString("parks");

            // date calculation dd-MM-yyyy hh:mm am/pm format
            try {
                // Create a SimpleDateFormat object for parsing the input date and time
                SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = sdfInput.parse(renewDate);

                // Create a SimpleDateFormat object for formatting the date and time in the desired format
                SimpleDateFormat sdfOutput = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                formattedRenewDateTime = sdfOutput.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            currentPlan.setText(plan);
            renewDateView.setText(formattedRenewDateTime);
            parkingHoursLeft.setText(parks);
            PlanName.setText(plan);

            if(plan.equals("Gold"))
            {
                planValidity.setText("30 Days");
                parkingHours.setText("30");
                pricing.setText("250");
            }
            else if(plan.equals("Platinum"))
            {
                planValidity.setText("60 days");
                parkingHours.setText("60");
                pricing.setText("650");

                planCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.platinumLight));
                PlanName.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
                planValidity.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
                parkingHours.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
                pricing.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
                planValidityHolder.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
                parkingHoursHolder.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
                pricingHolder.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            }
        });

        changePlanBtn.setOnClickListener(view->{
            Intent intent=new Intent(this, Subscription.class);
            startActivity(intent);
        });

        rechargeBtn.setOnClickListener(view->{
            Intent intent=new Intent(this, RechargeReciept.class);
            intent.putExtra("selectedPlan", plan);
            intent.putExtra("subscriptionChanged", "no");
            startActivity(intent);
        });
    }
}