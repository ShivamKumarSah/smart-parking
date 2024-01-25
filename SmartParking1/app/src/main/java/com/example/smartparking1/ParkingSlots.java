package com.example.smartparking1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ParkingSlots extends AppCompatActivity {
    private ListView listView;
    private List<HashMap<String, Object>> facilityList;
    private FacilityAdapter adapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_slots);
        listView = findViewById(R.id.listView);
        facilityList = new ArrayList<>();
        adapter = new FacilityAdapter(this, R.layout.list_item_table, facilityList);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchDataFromFirebase();

        // Set the click listener for the ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Retrieve the selected facility from the list
                HashMap<String, Object> selectedFacility = facilityList.get(position);

                // Check if selectedFacility is not null
                if (selectedFacility != null) {
                    // Extract the required information from the selectedFacility HashMap
                    String parkingName = Objects.requireNonNull(selectedFacility.get("name")).toString();
                    String address = Objects.requireNonNull(selectedFacility.get("Total Empty Slots")).toString();

                    // Create a Dialog or AlertDialog to show the popup card
                    AlertDialog.Builder builder = new AlertDialog.Builder(ParkingSlots.this);
                    View cardView = getLayoutInflater().inflate(R.layout.popup_card_layout, null);
                    builder.setView(cardView);

                    // Find the views inside the cardView
                    TextView titleTextView = cardView.findViewById(R.id.titleTextView);
                    TextView addressTextView = cardView.findViewById(R.id.addressTextView);

                    // Set the values for the views
                    titleTextView.setText(parkingName);
                    addressTextView.setText(address);

                    // Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void fetchDataFromFirebase() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                // Retrieve the facility object as a HashMap from the DataSnapshot
                HashMap<String, Object> facility = (HashMap<String, Object>) dataSnapshot.getValue();
                facilityList.add(facility);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Implement other ChildEventListener methods as needed

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur while retrieving data from Firebase
            }
        });
    }
}
