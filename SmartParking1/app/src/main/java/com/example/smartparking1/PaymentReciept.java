package com.example.smartparking1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PaymentReciept extends AppCompatActivity {
    private TextView parkingNameView, fromtime, toTime, transactionId, charge;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String UserId;
    private DocumentReference docRef;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_reciept);

        parkingNameView = findViewById(R.id.parkingNameView);
        fromtime = findViewById(R.id.fromtime);
        toTime = findViewById(R.id.toTime);
        transactionId = findViewById(R.id.transactionId);
        charge = findViewById(R.id.charge);
        Intent intent = getIntent();
        String payable = intent.getStringExtra("payable");

        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        mFirestore = FirebaseFirestore.getInstance();
        docRef = mFirestore.collection("users").document(UserId).collection("bookings").document(UserId);

        mFirestore.collection("users").document(UserId).collection("bookings").document(UserId).get().addOnSuccessListener(documentSnapshot -> {
            String parkingName = documentSnapshot.getString("parkingName");
            String floorNo = documentSnapshot.getString("floorNo");
            String slotNo = documentSnapshot.getString("slotNo");
            String parkedFromTime = documentSnapshot.getString("parkedFrom");
            String parkedToTime = documentSnapshot.getString("parkedTo");
            String paid = documentSnapshot.getString("paid");
            String facilityId = documentSnapshot.getString("facilityId");

            path = facilityId + "/Slots/" + floorNo + "/" + slotNo;

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference(path);

            HashMap<String, Object> slotData = new HashMap<>();
            slotData.put("status", "");
            slotData.put("bookedAt", "");
            slotData.put("bookedBy", "");

            // Insert the data into the database
            myRef.setValue(slotData).addOnCompleteListener(task -> {
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                    });

            // data updation on firestore
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

            docRef.update(updatedData).addOnSuccessListener(aVoid -> {
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    });

            parkingNameView.setText(parkingName);
            fromtime.setText(parkedFromTime);
            toTime.setText(parkedToTime);
            transactionId.setText("____");
            charge.setText(payable);
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Secondpage.class);
        startActivity(intent);
        finish();
    }
}
