package com.example.smartparking1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BookingActivity extends AppCompatActivity {
    private TextView usernameTextView, phoneTextView, carNoTextView, carNameTextView, licenseTextView,updateProfile, parkingNameView, parkingLocationView, floorDetailsView, slotDetailsView;
    private Button bookingBtn, cancelBtn;
    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId, floorNumber, slotNumber,parkingName, address, parentKey;
    private String bookedAtCheck, paidCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        usernameTextView = findViewById(R.id.loggedUser);
        phoneTextView = findViewById(R.id.mobileNo);
        carNoTextView = findViewById(R.id.carNumber);
        carNameTextView = findViewById(R.id.carName);
        licenseTextView = findViewById(R.id.licenseNo);
        updateProfile = findViewById(R.id.updateDetails);
        parkingNameView = findViewById(R.id.parkingName);
        parkingLocationView = findViewById(R.id.parkingLocation);
        slotDetailsView = findViewById(R.id.slotDetails);
        floorDetailsView = findViewById(R.id.floorDetails);
        bookingBtn = findViewById(R.id.bookingBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

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

        updateProfile.setOnClickListener(v -> {
            Intent intent=new Intent(BookingActivity.this, UpdateActivity.class);
            startActivity(intent);
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("selectedFacility")) {
            HashMap<String, Object> selectedFacility = (HashMap<String, Object>) extras.getSerializable("selectedFacility");
            parkingName = Objects.requireNonNull(selectedFacility.get("name")).toString();
            address = Objects.requireNonNull(selectedFacility.get("location")).toString();
            floorNumber = Objects.requireNonNull(selectedFacility.get("floor")).toString();
            slotNumber = Objects.requireNonNull(selectedFacility.get("slot")).toString();

            parkingNameView.setText(parkingName);
            parkingLocationView.setText(address);
            slotDetailsView.setText(slotNumber);
            floorDetailsView.setText(floorNumber);
        }

        bookingBtn.setOnClickListener(v -> {
        mFirestore.collection("users").document(UserId).collection("bookings").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
            bookedAtCheck = documentSnapshot.getString("bookedAt");
            paidCheck  = documentSnapshot.getString("paid");

                if(paidCheck.equals("Canceled") || bookedAtCheck.equals(""))
                {
                    externalFunction();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                    builder.setTitle("Attention! You have already made a booking.")
                            .setMessage("It seems that you have an existing booking in progress.")
                            .setPositiveButton("Okay", (dialog, which) -> {
                                Intent intent = new Intent(BookingActivity.this, PaymentOnline.class);
                                startActivity(intent);
                                dialog.dismiss();
                            })
                            .show();
                }
            });
        });

        cancelBtn.setOnClickListener(v -> {
            Intent intent = new Intent(BookingActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        });
    }

    public void externalFunction()
    {
        mFirestore.collection("users").document(UserId).collection("membership").document(UserId).get().addOnSuccessListener(documentSnapshot2 ->
        {
            String plan = documentSnapshot2.getString("plan");
            String expiryDate  = documentSnapshot2.getString("renewDate");
            String parkingHours  = documentSnapshot2.getString("parks");

            if (!(plan.equals("Free"))) {
                long timeLeft = gatVailidity(expiryDate);
                if (!parkingHours.equals("0") && timeLeft >= 1) {
                    updateToDataBase();
                } else if (timeLeft < 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                    builder.setTitle("No Active Plans")
                            .setMessage("Your plan has been expired, now the standard charges will apply.\nRecharge now avoid extra charges.")
                            .setPositiveButton("Confirm", (dialog, which) -> {
                                updateToDataBase();
                            dialog.dismiss();
                            }).setNegativeButton("Cancel", (dialog, which) -> {dialog.dismiss();}).show();
                } else if (parkingHours.equals("0")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                    builder.setTitle("Parking-Hours Exhausted")
                            .setMessage("You have used all your parking hours, now the standard charges will apply.\nRecharge now to get more parking hours")
                            .setPositiveButton("Confirm", (dialog, which) -> {
                                updateToDataBase();
                                dialog.dismiss();
                            }).setNegativeButton("Cancel", (dialog, which) -> {
                                dialog.dismiss();
                            })
                            .show();
                }
            } else {
                updateToDataBase();
            }
        });
    }

    public long gatVailidity(String expirydate)
    {
        String expiryDateString = expirydate; // Replace with your expiryDate string

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTimeString = formatter.format(new Date());
        Date renewDate = null, currentDate = null;
        try {
            renewDate = formatter.parse(expiryDateString);
            currentDate = new Date();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Calculate the difference between the two dates in milliseconds
        long differenceInMillis = renewDate.getTime() - currentDate.getTime();
        long seconds = (differenceInMillis % (1000 * 60)) / 1000;

        return seconds;
    }

    public void updateToDataBase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference();
        Query query = reference.orderByChild("name").equalTo(parkingName);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Iterate over the matching child nodes
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Get the parent node key
                        parentKey = childSnapshot.getKey();
                            Calendar calendar = Calendar.getInstance();
                            long bookedAtLong = calendar.getTimeInMillis();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date(bookedAtLong);
                            String bookedAt = sdf.format(date);

                            HashMap<String, Object> slotData = new HashMap<>();
                            slotData.put("status", "booked");
                            slotData.put("bookedAt", bookedAt);
                            slotData.put("bookedBy", UserId);

                            String path = parentKey + "/Slots/" + floorNumber + "/" + slotNumber;
                            DatabaseReference slotRef = reference.child(path);

                            // Insert the data into the database
                            slotRef.setValue(slotData)
                                    .addOnSuccessListener(aVoid ->
                                    {
                                        db = FirebaseFirestore.getInstance();
                                        mAuth = FirebaseAuth.getInstance();
                                        UserId = mAuth.getCurrentUser().getUid();
                                        docRef = db.collection("users").document(UserId).collection("bookings").document(UserId);

                                        Map<String, Object> firestoreUpdatedData = new HashMap<>();
                                            firestoreUpdatedData.put("facilityId", parentKey);
                                            firestoreUpdatedData.put("parkingName", parkingName);
                                            firestoreUpdatedData.put("location", address);
                                            firestoreUpdatedData.put("floorNo", floorNumber);
                                            firestoreUpdatedData.put("slotNo", slotNumber);
                                            firestoreUpdatedData.put("bookedAt", bookedAt);
                                            firestoreUpdatedData.put("paid", "No");

                                        docRef.update(firestoreUpdatedData)
                                                .addOnSuccessListener(s -> {
                                                    Intent intent=new Intent(BookingActivity.this, PaymentSuccessful.class);
                                                    startActivity(intent);
                                                })
                                                .addOnFailureListener(e -> {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(BookingActivity.this);
                                                    builder.setTitle("Alert")
                                                            .setMessage("Something went wrong! Can't book.")
                                                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                                                            .show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error conditions
                Toast.makeText(BookingActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BookingActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }
}