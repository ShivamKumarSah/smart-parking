package com.example.smartparking1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {
    private EditText nameText, emailText, phoneText, carNameText, carNoText;
    private Button updateButton;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    DocumentReference docRef;
    private String UserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        nameText= findViewById(R.id.personName);
        emailText= findViewById(R.id.email);
        phoneText= findViewById(R.id.mobile);
        carNameText= findViewById(R.id.carName);
        carNoText= findViewById(R.id.carNumber);
        updateButton= findViewById(R.id.updateButton);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        UserId = mAuth.getCurrentUser().getUid();
        docRef = db.collection("users").document(UserId);

        updateButton.setOnClickListener(v -> {
            String nameEntered = nameText.getText().toString().trim();
            String emailEntered = emailText.getText().toString().trim();
            String phoneEntered = phoneText.getText().toString().trim();
            String carNameEntered = carNameText.getText().toString().trim();
            String carNoEntered = carNoText.getText().toString().trim();

            Map<String, Object> updatedData = new HashMap<>();
            if(!nameEntered.isEmpty()) {
                updatedData.put("name", nameEntered);
            }
            if(!emailEntered.isEmpty()) {
                updatedData.put("email", emailEntered);
            }
            if(!phoneEntered.isEmpty()) {
                updatedData.put("phone", phoneEntered);
            }
            if(!carNameEntered.isEmpty()) {
                updatedData.put("carName", carNameEntered);
            }
            if(!carNoEntered.isEmpty()) {
                updatedData.put("carNo", carNoEntered);
            }

            docRef.update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        // Update successful
                        Toast.makeText(getApplicationContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent( UpdateActivity.this, ProfileActivity.class);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        // Update failed
                        Toast.makeText(getApplicationContext(), "Failed to update data", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
