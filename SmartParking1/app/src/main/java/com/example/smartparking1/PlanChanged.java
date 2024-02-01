package com.example.smartparking1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PlanChanged extends AppCompatActivity {
    private LottieAnimationView lottieAnimationView;
    private TextView planChangeDesc;
    private  String selectedPlan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_changed);

        lottieAnimationView=findViewById(R.id.lottieAnimationView);
        planChangeDesc=findViewById(R.id.planChangeDesc);

        Intent intent = getIntent();
        selectedPlan = intent.getStringExtra("selectedPlan");

        if(selectedPlan.equals("Platinum")){
            lottieAnimationView.setAnimation(R.raw.platinum_plan);
            planChangeDesc.setText("You are a Platinum Member now");
        }
        else if(selectedPlan.equals("Gold")){
            planChangeDesc.setText("You are a Gold Member now");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RechargeReciept.class);
        intent.putExtra("selectedPlan", selectedPlan);
        intent.putExtra("subscriptionChanged", "no");
        startActivity(intent);
        finish();
    }
}