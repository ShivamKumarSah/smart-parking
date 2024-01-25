package com.example.smartparking1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class FacilityAdapter extends ArrayAdapter<HashMap<String, Object>> {
    private LayoutInflater inflater;
    private int resource;

    public FacilityAdapter(Context context, int resource, List<HashMap<String, Object>> facilities) {
        super(context, resource, facilities);
        inflater = LayoutInflater.from(context);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(resource, parent, false);
        }

        HashMap<String, Object> facility = getItem(position);

        TextView parkingName = view.findViewById(R.id.parkingName);
//        TextView textSlot1 = view.findViewById(R.id.textSlot1);
//        TextView textSlot2 = view.findViewById(R.id.textSlot2);
//        TextView textSlot3 = view.findViewById(R.id.textSlot3);
        TextView parkingAddress = view.findViewById(R.id.parkingAddress);
        TextView totalSlotsAvailable = view.findViewById(R.id.totalSlotsAvailable);

//        TextView textName = view.findViewById(R.id.textName);
//        TextView textSlot1 = view.findViewById(R.id.textSlot1);
//        TextView textSlot2 = view.findViewById(R.id.textSlot2);
//        TextView textSlot3 = view.findViewById(R.id.textSlot3);
//        TextView textSlot4 = view.findViewById(R.id.textSlot4);
//        TextView textTotalSlots = view.findViewById(R.id.textTotalSlots);

//        parkingName.setText((String) facility.get("name"));
////        textSlot1.setText((String) facility.get("Slot 1"));
////        textSlot2.setText((String) facility.get("Slot 2"));
////        textSlot3.setText((String) facility.get("Slot 3"));
//        parkingAddress.setText((String) facility.get("location"));
//        totalSlotsAvailable.setText(String.valueOf(facility.get("Total Empty Slots")));


        parkingName.setText((String) facility.get("name"));
//        textSlot1.setText(String.valueOf(facility.get("Slot 1")));
//        textSlot2.setText(String.valueOf(facility.get("Slot 2")));
//        textSlot3.setText(String.valueOf(facility.get("Slot 3")));
        parkingAddress.setText(String.valueOf(facility.get("location")));
        totalSlotsAvailable.setText(String.valueOf(facility.get("Total Empty Slots")));

        return view;
    }
}
