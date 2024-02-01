package com.example.smartparking1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
public class RechargeReciept extends AppCompatActivity {
    private TextView rechargedPlan, gotParkingHours, gotValidity, nextExpiryDate, paidAmount, transactionIdNo, rechargedOn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private FirebaseAuth mAuth;
    private String UserId,parkingCharges, parkingHours, planCharges, renewDateTime;
    private Handler handler;
    private int validity;
    private long timeLeft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_reciept);

        rechargedPlan=findViewById(R.id.rechargedPlan);
        gotParkingHours=findViewById(R.id.gotParkingHours);
        gotValidity=findViewById(R.id.gotValidity);
        nextExpiryDate=findViewById(R.id.nextExpiryDate);
        paidAmount=findViewById(R.id.paidAmount);
        transactionIdNo=findViewById(R.id.transactionIdNo);
        rechargedOn=findViewById(R.id.rechargedOn);

        Intent intent = getIntent();
        String selectedPlan = intent.getStringExtra("selectedPlan");
        String subscriptionChanged = intent.getStringExtra("subscriptionChanged");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        docRef = db.collection("users").document(UserId).collection("membership").document(UserId);

        if(selectedPlan.equals("Platinum")){
            parkingCharges="20";
            parkingHours="150";
            planCharges="2500";
            validity=60;
        }
        else if(selectedPlan.equals("Gold")){
            parkingCharges="20";
            parkingHours="60";
            planCharges="1000";
            validity=30;
        }

        Calendar calendar = Calendar.getInstance();
        long bookedAtLong = calendar.getTimeInMillis();
        Date currentDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, validity);
        SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdfDateTime.format(currentDate);
        renewDateTime = sdfDateTime.format(calendar.getTime());

//        Toast.makeText(this, renewDateTime, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Validity" + validity, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Parking Hrs"+ parkingHours, Toast.LENGTH_SHORT).show();

        db.collection("users").document(UserId).collection("membership").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
            String renewDate = documentSnapshot.getString("renewDate");
            String leftHours = documentSnapshot.getString("parks");
            Map<String, Object> firestoreUpdatedData = new HashMap<>();
            timeLeftToExpirePlan(renewDate);
            if(timeLeft>0 && !leftHours.equals("0"))
            {
                int parkingHoursInt=Integer.parseInt(parkingHours);
                int leftHoursInt=Integer.parseInt(leftHours);
                String updatablePHrs=Integer.toString(parkingHoursInt+leftHoursInt);
                String newrenewDate=addDaysToDate(renewDateTime, validity);

                firestoreUpdatedData.put("renewDate", newrenewDate);
                firestoreUpdatedData.put("parks", updatablePHrs);
            }
            else {
                firestoreUpdatedData.put("renewDate", renewDateTime);
                firestoreUpdatedData.put("parks", parkingHours);
            }
            firestoreUpdatedData.put("parkingCharges", parkingCharges);
            firestoreUpdatedData.put("plan", selectedPlan);
            firestoreUpdatedData.put("planCharges", planCharges);
            firestoreUpdatedData.put("rechargeDate", currentDateTime);

            docRef.update(firestoreUpdatedData)
                    .addOnSuccessListener(s -> {
                        // Update successful
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this,"failed", Toast.LENGTH_SHORT).show();
                    });
        });
        rechargedPlan.setText(selectedPlan);
        gotParkingHours.setText(parkingHours);
        gotValidity.setText(Integer.toString(validity));
        nextExpiryDate.setText(renewDateTime);
        paidAmount.setText(planCharges);
        transactionIdNo.setText("");
        rechargedOn.setText(currentDateTime);

        if(subscriptionChanged.equals("yes")){
            handler = new Handler();
            handler.postDelayed(() -> navigateToDestinationActivity(selectedPlan), 1000);
        }
    }

    public void timeLeftToExpirePlan(String renewDate){
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Parse the given string date into a Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date givenDate;
        try {
            givenDate = dateFormat.parse(renewDate);
            long timeDifferenceInMillis = givenDate.getTime() - currentDate.getTime();
            timeLeft = timeDifferenceInMillis / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void navigateToDestinationActivity(String selectedPlan) {
        Intent intent = new Intent(this, PlanChanged.class);
        intent.putExtra("selectedPlan", selectedPlan);
        startActivity(intent);
        finish();
    }

    public String addDaysToDate(String renewDateTime, int validity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date inputDate = sdf.parse(renewDateTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(inputDate);
            calendar.add(Calendar.DAY_OF_MONTH, validity);
            Date resultDate = calendar.getTime();
            String resultDateStr = sdf.format(resultDate);

            return resultDateStr;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }
}