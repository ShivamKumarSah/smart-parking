package com.example.smartparking1;

public class Slot {
    private String slotName;
    private String slotStatus;

    public Slot(String slotStatus) {
        this.slotStatus = slotStatus;
    }
//    public Slot(String slotName, String slotStatus) {
//        this.slotName = slotName;
//        this.slotStatus = slotStatus;
//        this.slotStatus = slotStatus;
//    }

    public String getSlotName() {
        return slotName;
    }

    public String getSlotStatus() {
        return slotStatus;
    }
}
