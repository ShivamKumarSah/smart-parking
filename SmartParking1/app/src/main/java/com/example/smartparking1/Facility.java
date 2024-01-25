package com.example.smartparking1;

import java.util.Map;

public class Facility {
    private String name;
    private Map<String, String> slots;
    private int totalEmptySlots;

    public Facility() {
        // Default constructor required for Firebase
    }

    public Facility(String name, Map<String, String> slots, int totalEmptySlots) {
        this.name = name;
        this.slots = slots;
        this.totalEmptySlots = totalEmptySlots;
    }

    // Getters and setters for the attributes

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getSlots() {
        return slots;
    }

    public void setSlots(Map<String, String> slots) {
        this.slots = slots;
    }

    public int getTotalEmptySlots() {
        return totalEmptySlots;
    }

    public void setTotalEmptySlots(int totalEmptySlots) {
        this.totalEmptySlots = totalEmptySlots;
    }
}
