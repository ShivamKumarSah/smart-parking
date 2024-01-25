package com.example.smartparking1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GetSubscription extends AppCompatActivity {

    private CardView planCardView;
    private TextView PlanName, planInfo, t1, t2, t3, t4, t5, validity, Booking, maxBooking, normalCharges, planCharges, saving;
    private Button panBuyBtn;
    private String planValidity, parkingCharges, parkingHours, planValue, savingAmnt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_subscription);

        // Getting the selected plan from the previous screen
        Intent intent = getIntent();
        String selectedPlan = intent.getStringExtra("selectedPlan");
        String planDesc = intent.getStringExtra("planDesc");

        // getting the view id's
        planCardView=findViewById(R.id.planCardView);
        PlanName=findViewById(R.id.PlanName);
        planInfo=findViewById(R.id.planInfo);
        t1=findViewById(R.id.t1);
        t2=findViewById(R.id.t2);
        t3=findViewById(R.id.t3);
        t4=findViewById(R.id.t4);
        validity=findViewById(R.id.validity);
        Booking=findViewById(R.id.Booking);
        maxBooking=findViewById(R.id.maxBooking);
        normalCharges=findViewById(R.id.normalCharges);
        planCharges=findViewById(R.id.planCharges);
        saving=findViewById(R.id.saving);
        panBuyBtn=findViewById(R.id.panBuyBtn);

        if(selectedPlan.equals("Gold"))
        {
            planCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.goldLight));
            PlanName.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            planInfo.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            t1.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            t2.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            t3.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            t4.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            validity.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            Booking.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            maxBooking.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            planCharges.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            saving.setTextColor(ContextCompat.getColor(this, R.color.golddark));
            panBuyBtn.setTextColor(ContextCompat.getColor(this, R.color.goldLight));
            panBuyBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.golddark));

            planValue="1000";
            planValidity="30 days";
            parkingCharges="20";
            parkingHours="60";
        }
        else if (selectedPlan.equals("Platinum"))
        {
            planCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.platinumLight));
            PlanName.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            planInfo.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            t1.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            t2.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            t3.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            t4.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            validity.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            Booking.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            maxBooking.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            planCharges.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            saving.setTextColor(ContextCompat.getColor(this, R.color.platinumDark));
            panBuyBtn.setTextColor(ContextCompat.getColor(this, R.color.platinumLight));
            panBuyBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.platinumDark));

            planValue="2500";
            planValidity="60 days";
            parkingCharges="20";
            parkingHours="150";
        }

        int parkingChargesInt=Integer.parseInt(parkingCharges);
        int parkingHoursint=Integer.parseInt(parkingHours);
        int planValueInt=Integer.parseInt(planValue);

        int shouldChargeInt=parkingChargesInt*parkingHoursint;

        String shouldCharge = Integer.toString(shouldChargeInt);
        String savingAmnt = Integer.toString(shouldChargeInt-planValueInt);

        PlanName.setText(selectedPlan);
        planInfo.setText(planDesc);
        validity.setText(planValidity);
        Booking.setText(parkingCharges+"/hour");
        maxBooking.setText(parkingHours+" hours");
        normalCharges.setText(parkingCharges +" X " +parkingHours +" = " +shouldCharge+" /-");
        planCharges.setText(planValue+"/-");
        panBuyBtn.setText("Pay "+planValue+"/- to get "+selectedPlan+" Plan");
        saving.setText(savingAmnt+"/-");

        // When user click on the button
        panBuyBtn.setOnClickListener(view->{
            Intent intent1=new Intent(this, RechargeReciept.class);
            intent1.putExtra("selectedPlan", selectedPlan);
            intent1.putExtra("subscriptionChanged", "yes");
            startActivity(intent1);
        });



    }
}