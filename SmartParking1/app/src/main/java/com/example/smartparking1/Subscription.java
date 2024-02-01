package com.example.smartparking1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class Subscription extends AppCompatActivity {
    private TextView currentPlan, goldDesc, platinumDesc;
    private Button freePlanBtn, goldPlan, platinumPlan;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private FirebaseAuth mAuth;
    private long timeLeft;
    private String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        currentPlan= findViewById(R.id.currentPlan);
        goldPlan= findViewById(R.id.goldPlan);
        platinumPlan= findViewById(R.id.platinumPlan);
        goldDesc= findViewById(R.id.goldDesc1);
        platinumDesc= findViewById(R.id.platinumDesc1);
        freePlanBtn= findViewById(R.id.freePlan);

        Map<String, Object> firestoreUpdatedData = new HashMap<>();
        firestoreUpdatedData.put("renewDate", "");
        firestoreUpdatedData.put("parkingCharges", "");
        firestoreUpdatedData.put("parks", "0");
        firestoreUpdatedData.put("plan", "Free");
        firestoreUpdatedData.put("planCharges", "");
        firestoreUpdatedData.put("rechargeDate", "");

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        docRef=db.collection("users").document(UserId).collection("membership").document(UserId);

        db.collection("users").document(UserId).collection("membership").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
            String planName = documentSnapshot.getString("plan");
            String renewDate = documentSnapshot.getString("renewDate");
            String parkingHrs = documentSnapshot.getString("parks");
            currentPlan.setText(planName);
            timeLeftToExpirePlan(renewDate);

            if(timeLeft>0 && !parkingHrs.equals("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Already Have an Active Plan.")
                        .setMessage(" If you want to change the plan, you can do it once the active plan expires.")
                        .setPositiveButton("Ok", (dialog, which) ->
                        {
                            Intent intent = new Intent(this, Secondpage.class);
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .show();
            }
            else{
                if(planName.equals("Free"))
                {
                    freePlanBtn.setVisibility(View.INVISIBLE);
                }
                else if(planName.equals("Gold"))
                {
                    goldPlan.setVisibility(View.INVISIBLE);
                }
                else if(planName.equals("Platinum"))
                {
                    platinumPlan.setVisibility(View.INVISIBLE);
                }
                    goldPlan.setOnClickListener(view -> {
                        Intent intent = new Intent(this, GetSubscription.class);
                        intent.putExtra("selectedPlan", "Gold");
                        intent.putExtra("planDesc", goldDesc.getText());
                        startActivity(intent);
                    });

                    freePlanBtn.setOnClickListener(view -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("You selected the Free plan.")
                                .setMessage("Without the premium plans, you will no longer have access to the benefits they offered.")
                                .setPositiveButton("Confirm", (dialog, which) ->
                                {
                                    docRef.update(firestoreUpdatedData)
                                            .addOnSuccessListener(s -> {
                                                // Update successful
                                                Intent intent = new Intent(this, ProfileActivity.class);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this,"failed", Toast.LENGTH_SHORT).show();
                                            });
                                }).setNegativeButton("Cancel", (dialog, which) ->dialog.dismiss())
                                .show();
                    });

                    platinumPlan.setOnClickListener(view -> {
                        Intent intent = new Intent(this, GetSubscription.class);
                        intent.putExtra("selectedPlan", "Platinum");
                        intent.putExtra("planDesc", platinumDesc.getText());
                        startActivity(intent);
                    });
            }
        });
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
}
