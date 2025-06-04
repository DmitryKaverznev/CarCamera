package com.dkaverznev.carcamera.data.vehicles;

import androidx.annotation.NonNull;

import java.util.Map;

public class Vehicle {
    public String note;
    public String status;
    public Map<String, String> info;

    public Vehicle() {
    }

    public Vehicle(String note, String status, Map<String, String> info) {
        this.note = note;
        this.status = status;
        this.info = info;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, String> getInfo() { return info; }
    public void setInfo(Map<String, String> info) { this.info = info; }

    @NonNull
    @Override
    public String toString() {
        return "Vehicle{" +
                "note='" + (note != null ? note : "null") + '\'' +
                ", status='" + (status != null ? status : "null") + '\'' +
                ", info=" + (info != null ? info.toString() : "null") +
                '}';
    }
}