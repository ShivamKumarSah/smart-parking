package com.example.smartparking1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;
import java.util.Objects;

public class SlotChoose extends AppCompatActivity {

    private TextView parkingNameBooking, parkingLocationBooking;
    CardView slotCardView;
    private CardView selectedSlotCardView;

    String selectedSlot = "", selectedFloor;
    LinearLayout linearLayout;
    HashMap<String, Object> selectedFacility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_choose);

        parkingNameBooking = findViewById(R.id.parkingNameBooking);
        parkingLocationBooking = findViewById(R.id.parkingLocationBooking);

        // Inside the BookingActivity's onCreate() method
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("selectedFacility")) {
            selectedFacility = (HashMap<String, Object>) extras.getSerializable("selectedFacility");
            String parkingName = Objects.requireNonNull(selectedFacility.get("name")).toString();
            String address = Objects.requireNonNull(selectedFacility.get("location")).toString();
            HashMap<String, Object> floorList = (HashMap<String, Object>) Objects.requireNonNull(selectedFacility.get("Slots"));

            parkingNameBooking.setText(parkingName);
            parkingLocationBooking.setText(address);
            linearLayout = findViewById(R.id.floorView); // Replace with the ID of your LinearLayout

            for (String key : floorList.keySet()) {
                TextView floorNumberTextView = new TextView(this);
                CardView floorCardView = new CardView(this); // Replace "this" with the appropriate context

                // Customize the floor CardView
                LinearLayout.LayoutParams floorLayoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                floorLayoutParams.setMargins(30, 10, 30, 30); // Example margin values
                floorLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                floorCardView.setLayoutParams(floorLayoutParams);
                floorNumberTextView.setLayoutParams(floorLayoutParams);
                floorCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.purple)); // Example background color
                floorNumberTextView.setTextColor(ContextCompat.getColor(this, R.color.purple));
                floorCardView.setCardElevation(18); // Example elevation
                floorCardView.setRadius(15);

                floorNumberTextView.setText(key);
                // Create a LinearLayout for slots
                FlexboxLayout slotsLayout = new FlexboxLayout(this);
                FlexboxLayout.LayoutParams slotsLayoutParams = new FlexboxLayout.LayoutParams(
                        FlexboxLayout.LayoutParams.WRAP_CONTENT,
                        FlexboxLayout.LayoutParams.WRAP_CONTENT
                );
                slotsLayoutParams.setMargins(30, 30, 30, 30); // Example margin values
                slotsLayout.setLayoutParams(slotsLayoutParams);

                // Retrieve the slots HashMap for the current floor
                HashMap<String, Object> slotsMap = (HashMap<String, Object>) floorList.get(key);
                if (slotsMap != null) {
                    for (String slotKey : slotsMap.keySet()) {
                        slotCardView = new CardView(this);

                        // Customize the slot CardView
                        LinearLayout.LayoutParams slotLayoutParams = new LinearLayout.LayoutParams(
                                250, 150
                        );
                        slotLayoutParams.setMargins(20, 12, 20, 12); // Example margin values

                        slotCardView.setLayoutParams(slotLayoutParams);
                        slotCardView.setCardElevation(18); // Example elevation
                        slotCardView.setRadius(10);

                        // Check if slot value is "Empty" and set background color accordingly
                        Object slotValue = slotsMap.get(slotKey);

                        if (slotValue instanceof HashMap) {
                            HashMap<String, Object> slotMap = (HashMap<String, Object>) slotValue;
                            Object slotStatus = slotMap.get("status");
                            if (slotStatus != null && slotStatus.equals("")) {
                                // Slot is booked
                                slotCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white));
                            } else {
                                // Slot is available
                                slotCardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.red)); // Example background color
                            }
                        }

                        // Create a TextView for the slot
                        TextView slotTextView = new TextView(this);
                        LinearLayout.LayoutParams slotTextLayoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        slotTextLayoutParams.width = 250; // Adjust the width as desired
                        slotTextLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        slotTextView.setLayoutParams(slotTextLayoutParams);
                        slotTextView.setText(slotKey);

                        // Add the slot TextView to the slot CardView
                        slotCardView.addView(slotTextView);

                        // Add the slot CardView to the slots LinearLayout
                        slotsLayout.addView(slotCardView);

                        slotCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (slotValue instanceof HashMap) {
                                    HashMap<String, Object> slotMap = (HashMap<String, Object>) slotValue;
                                    Object slotStatus = slotMap.get("status");
                                if (slotStatus != null && slotStatus.equals("")) {
                                    selectedSlot = slotKey;
                                    selectedFloor = key;

                                    // Update the selectedFacility HashMap
                                    selectedFacility.put("floor", selectedFloor);
                                    selectedFacility.put("slot", selectedSlot);

                                    String message = "Selected Slot: " + selectedSlot + "\nSelected Floor: " + selectedFloor;

                                    AlertDialog.Builder builder = new AlertDialog.Builder(SlotChoose.this);
                                    builder.setTitle("Confirmation")
                                            .setMessage(message)
                                            .setPositiveButton("OK", (dialog, which) -> {
                                                Intent intent = new Intent(SlotChoose.this, BookingActivity.class);
                                                intent.putExtra("selectedFacility", selectedFacility);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            })
                                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                                            .show();
                                }
                                else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SlotChoose.this);
                                    builder.setTitle("Alert")
                                            .setMessage("This Slot is Full!")
                                            .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss())
                                            .show();
                                }
                                }
                            }
                        });
                    }
                }
                // Add the slots LinearLayout to the floor CardView
                floorCardView.addView(slotsLayout);

                // Add the floor CardView to the LinearLayout
                linearLayout.addView(floorNumberTextView);
                linearLayout.addView(floorCardView);
            }
        }
    }
}
