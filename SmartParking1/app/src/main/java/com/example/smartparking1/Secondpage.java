package com.example.smartparking1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Secondpage extends AppCompatActivity {

    private CardView button1, button2, button3, button4, button5, button6;
    private CardView progCard;
    private ProgressBar loaderHorizontal;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId;
    boolean isDarkMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondpage);

        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();

        button1 = findViewById(R.id.parkingCard);
        button2 = findViewById(R.id.paymentCard);
        button3 = findViewById(R.id.profileCard);
        button4 = findViewById(R.id.bookings);
        button5 = findViewById(R.id.subscriptionCard);
        button6 = findViewById(R.id.logoutCard);
//        CardView themeCard = findViewById(R.id.darkModeButton);
//        ImageView themeImageView = findViewById(R.id.themeIcon);
//        TextView themeTextView = findViewById(R.id.themeText);
        loaderHorizontal = findViewById(R.id.loader_horizontal);
        progCard=findViewById(R.id.progCard);

//        themeCard.setOnClickListener(view-> {
////            progCard.setVisibility(View.VISIBLE);
////
////            // show and start progressbar
////            new Thread(() -> {
////                for (int progress = 0; progress <= 100; progress++) {
////                    updateProgressBar(progress);
////
////                    // Check if progress is 100% and show Toast
////                    if (progress == 100) {
////                        runOnUiThread(() -> Toast.makeText(this, "Progress completed!", Toast.LENGTH_SHORT).show());
////                    }
////                    try {
////                        Thread.sleep(15);
////                    } catch (InterruptedException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }).start();
//
//
//
//        });

        button1.setOnClickListener(view -> {
            Intent intent = new Intent(Secondpage.this, MapsActivity.class);
            startActivity(intent);
        });

        button2.setOnClickListener(view -> {
            progCard.setVisibility(View.VISIBLE);

            // show and start progressbar
            new Thread(() -> {
                for (int progress = 0; progress <= 100; progress++) {
                    updateProgressBar(progress);

                    // Check if progress is 100% and show Toast
                    if (progress == 100) {
                        progCard.setVisibility(View.INVISIBLE);
                        mFirestore.collection("users").document(UserId).collection("bookings").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
                            String bookedAt  = documentSnapshot.getString("bookedAt");
                            if(bookedAt.equals("")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(Secondpage.this);
                                builder.setTitle("No Bookings!")
                                        .setMessage("You must book your slot first.")
                                        .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                                        .show();
                            }
                            else {
                                Intent intent = new Intent(Secondpage.this, PaymentOnline.class);
                                startActivity(intent);
                            }
                        });
                    }
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });

        button3.setOnClickListener(view -> {
            Intent intent = new Intent(Secondpage.this, ProfileActivity.class);
            startActivity(intent);
        });

        button4.setOnClickListener(view -> {
            mFirestore.collection("users").document(UserId).collection("membership").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
                String currentPlan  = documentSnapshot.getString("plan");
                if(currentPlan.equals("Free")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Secondpage.this);
                    builder.setTitle("You have FREE Plan")
                            .setMessage("You can get the benefit by getting Premium Plans in plan section.")
                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                            .show();
                }
                else {
                    Intent intent = new Intent(Secondpage.this, Recharge.class);
                    startActivity(intent);
                }
            });
        });

        button5.setOnClickListener(view -> {
            Intent intent = new Intent(Secondpage.this, Subscription.class);
            startActivity(intent);
        });

        button6.setOnClickListener(view -> logout());
    }

    private void updateProgressBar(final int progress) {
        runOnUiThread(() -> loaderHorizontal.setProgress(progress));
    }
    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", false);
        editor.apply();

        // Redirect to LoginActivity or any other desired screen
        Intent intent = new Intent(Secondpage.this, MainActivity.class);
        startActivity(intent);
        finish(); // Optional: Close the current activity
    }
}
