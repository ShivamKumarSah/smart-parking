package com.example.smartparking1;

        import androidx.appcompat.app.AlertDialog;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.content.ContextCompat;

        import android.content.Intent;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.Query;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.firestore.DocumentReference;
        import com.google.firebase.firestore.FirebaseFirestore;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;
        import java.util.Objects;
        import java.util.concurrent.TimeUnit;

public class PaymentOnline extends AppCompatActivity {
    private TextView usernameTextView, phoneTextView, carNoTextView, carNameTextView, licenseTextView,updateProfile, parkingNameView, parkingLocationView, floorDetailsView, slotDetailsView, parkingCharges, parkedTo, parkedFrom;
    private Button bookingBtn;
    private TextView payTextView, toTimetextView, fromTextView, ParkedTextView, planBenifitTextView, parkedDuration, planBenifit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId, parentKey, facilityId, floorNo, slotNo;
    private DocumentReference docRef, docRefM;
    private long hours, durationLong, timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_online);

        usernameTextView = findViewById(R.id.loggedUser);
        phoneTextView = findViewById(R.id.mobileNo);
        carNoTextView = findViewById(R.id.carNumber);
        carNameTextView = findViewById(R.id.carName);
        licenseTextView = findViewById(R.id.licenseNo);

        parkingNameView = findViewById(R.id.parkingName);
        parkingLocationView = findViewById(R.id.parkingLocation);
        slotDetailsView = findViewById(R.id.slotDetails);
        floorDetailsView = findViewById(R.id.floorDetails);
        parkedFrom = findViewById(R.id.fromTime);
        parkedTo = findViewById(R.id.toTime);
        parkingCharges = findViewById(R.id.charge);
        payTextView = findViewById(R.id.payTextView);
        toTimetextView = findViewById(R.id.toTimetextView);
        fromTextView = findViewById(R.id.fromTextView);
        bookingBtn = findViewById(R.id.bookingBtn);
        ParkedTextView = findViewById(R.id.ParkedTextView);
        parkedDuration = findViewById(R.id.parkedDuration);
        planBenifitTextView = findViewById(R.id.planBenifitTextView);
        planBenifit = findViewById(R.id.planBenifit);

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
            phoneTextView.setText(phone);
            carNoTextView.setText(car_no);
            carNameTextView.setText(car_name);
            licenseTextView.setText(license);
        });
        docRef=mFirestore.collection("users").document(UserId).collection("bookings").document(UserId);
        docRefM=mFirestore.collection("users").document(UserId).collection("membership").document(UserId);

        mFirestore.collection("users").document(UserId).collection("bookings").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
            String parkingName  = documentSnapshot.getString("parkingName");
            String location  = documentSnapshot.getString("location");
            floorNo  = documentSnapshot.getString("floorNo");
            slotNo  = documentSnapshot.getString("slotNo");
            String bookedAt  = documentSnapshot.getString("bookedAt");
            String parkedFromTime  = documentSnapshot.getString("parkedFrom");
            String parkedToTime  = documentSnapshot.getString("parkedTo");
            String paid  = documentSnapshot.getString("paid");
            facilityId  = documentSnapshot.getString("facilityId");

            parkingNameView.setText(parkingName);
            parkingLocationView.setText(location);
            floorDetailsView.setText(floorNo);
            slotDetailsView.setText(slotNo);

            if (parkedFromTime.equals("") && paid.equals("No")) {
                payTextView.setVisibility(View.INVISIBLE);
                toTimetextView.setVisibility(View.INVISIBLE);
                fromTextView.setVisibility(View.INVISIBLE);
                parkedTo.setVisibility(View.INVISIBLE);
                parkingCharges.setVisibility(View.INVISIBLE);
                parkedFrom.setVisibility(View.INVISIBLE);
                ParkedTextView.setVisibility(View.INVISIBLE);
                parkedDuration.setVisibility(View.INVISIBLE);
                planBenifitTextView.setVisibility(View.INVISIBLE);
                planBenifit.setVisibility(View.INVISIBLE);

                bookingBtn.setText("Cancel Booking");
                bookingBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

                bookingBtn.setOnClickListener(v -> {
                    cancelBooking();
                });
            }
            else if(paid.equals("Canceled"))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Booking Canceled")
                        .setMessage("You canceled your previous booking.")
                        .setPositiveButton("Ok", (dialog, which) -> {
                            Intent intent=new Intent(this, Secondpage.class);
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .show();
            }
            else{
                parkedFrom.setText(parkedFromTime);
                parkedTo.setText(parkedToTime);

                mFirestore.collection("users").document(UserId).collection("membership").document(UserId).get().addOnSuccessListener(documentSnapshot2 -> {
                    String planHrs  = documentSnapshot2.getString("parks");
                    String plan  = documentSnapshot2.getString("plan");
                    String renewDate  = documentSnapshot2.getString("renewDate");

                    // To calculate the charges
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {
                        Date timestamp1 = dateFormat.parse(parkedFromTime);
                        Date timestamp2 = dateFormat.parse(parkedToTime);
                        long durationMillis = timestamp2.getTime() - timestamp1.getTime();
                        hours = TimeUnit.MILLISECONDS.toHours(durationMillis) + 1; // y
                        durationLong=hours;

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long charges = 20;

                    // to calculate timeLeftToExpirePlan
                    timeLeftToExpirePlan(renewDate);

                        if (plan.equals("Free") || timeLeft<0) {
                            planBenifitTextView.setVisibility(View.GONE);
                            planBenifit.setVisibility(View.GONE);
                        }
                        else{
                            long usedHrs;

                            long planHrsInt = Long.parseLong(planHrs); // x

                            if (planHrsInt >= hours) {
                                planHrsInt -= hours;
                                usedHrs = hours;
                                hours = 0;
                            } else {
                                hours -= planHrsInt;
                                usedHrs = planHrsInt;
                                planHrsInt = 0;
                            }

                            planHrs = Long.toString(planHrsInt);
                            String usedHrsStr = Long.toString(usedHrs);
                            planBenifit.setText("-"+usedHrsStr+"hrs");
                        }

                    String duration=Long.toString(durationLong);

                    String payable = Long.toString(hours * charges);

                    parkedDuration.setText(duration+"hrs");
                    parkingCharges.setText(payable+"/-");

                    HashMap<String, Object> parkHrsUpdate = new HashMap<>();
                    parkHrsUpdate.put("parks", planHrs);


                    if(payable.equals("0")){
                        bookingBtn.setText("Click to use plan benefit");
                        bookingBtn.setOnClickListener(v -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("You don't need to pay for this booking.")
                                    .setMessage("You have received the plan benefit. Check the My-Account for more info.")
                                    .setPositiveButton("Okay", (dialog, which) -> {
                                        payBooking();
                                        docRefM.update(parkHrsUpdate)
                                                .addOnSuccessListener(s -> {
                                                    Intent intent = new Intent(this, ProfileActivity.class);
                                                    intent.putExtra("payable", payable);
                                                    startActivity(intent);
                                                })
                                                .addOnFailureListener(e -> {
                                                    AlertDialog.Builder builderF = new AlertDialog.Builder(this);
                                                    builderF.setTitle("Alert")
                                                            .setMessage("Something went wrong! Can't book.")
                                                            .setPositiveButton("Ok", (dialogF, whichF) -> dialog.dismiss())
                                                            .show();
                                                });
                                        dialog.dismiss();
                                    })
                                    .show();
                        });
                    }else{
                        bookingBtn.setText("Pay "+payable+"/-");
                        bookingBtn.setOnClickListener(v -> {
                            Intent intent=new Intent(PaymentOnline.this, PaymentReciept.class);
                            intent.putExtra("payable", payable);
                            startActivity(intent);
                        });
                    }
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

    public void cancelBooking()
    {
        removeDataFromFirebase();
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("paid", "Canceled");
        docRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Booking Canceled")
                            .setMessage("You can book new parking")
                            .setPositiveButton("Ok", (dialog, which) -> {
                                Intent intent=new Intent(this, Secondpage.class);
                                startActivity(intent);
                                dialog.dismiss();
                            })
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
    }

    public void payBooking()
    {
        removeDataFromFirebase();
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("bookedAt", "");
        updatedData.put("facilityId", "");
        updatedData.put("floorNo", "");
        updatedData.put("location", "");
        updatedData.put("paid", "");
        updatedData.put("parkedFrom", "");
        updatedData.put("parkedTo", "");
        updatedData.put("parkingName", "");
        updatedData.put("slotNo", "");

        docRef.update(updatedData)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                });
    }

    public void removeDataFromFirebase() {
        String path = facilityId + "/Slots/" + floorNo + "/" + slotNo;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(path);

        HashMap<String, Object> slotData = new HashMap<>();
        slotData.put("status", "");
        slotData.put("bookedAt", "");
        slotData.put("bookedBy", "");

//        myRef.updateChildren(slotData);

        // Insert the data into the database
        myRef.setValue(slotData).addOnCompleteListener(task -> {
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PaymentOnline.this, Secondpage.class);
        startActivity(intent);
        finish();
    }

}