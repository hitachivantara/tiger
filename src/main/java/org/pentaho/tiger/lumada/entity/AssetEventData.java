package org.pentaho.tiger.lumada.entity;

import com.google.gson.annotations.SerializedName;

public class AssetEventData {
    @SerializedName("string_event")
    private String stringEvent;
    @SerializedName("boolean_event")
    private boolean booleanEvent;
    @SerializedName("int_event")
    private int intEvent;
    @SerializedName("double_event")
    private double doubleEvent;

    public String getStringEvent() {
        return stringEvent;
    }

    public void setStringEvent(String stringEvent) {
        this.stringEvent = stringEvent;
    }

    public boolean isBooleanEvent() {
        return booleanEvent;
    }

    public void setBooleanEvent(boolean booleanEvent) {
        this.booleanEvent = booleanEvent;
    }

    public int getIntEvent() {
        return intEvent;
    }

    public void setIntEvent(int intEvent) {
        this.intEvent = intEvent;
    }

    public double getDoubleEvent() {
        return doubleEvent;
    }

    public void setDoubleEvent(double doubleEvent) {
        this.doubleEvent = doubleEvent;
    }
}
