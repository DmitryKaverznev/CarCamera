package com.dkaverznev.carcamera.data;

import androidx.annotation.NonNull;

import java.util.List;

public class Vehicle {
    private boolean status;
    private String comment;
    private List<String> info;

    public Vehicle() {}

    public Vehicle(boolean status, String comment, List<String> info) {
        this.status = status;
        this.comment = comment;
        this.info = info;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }

    @NonNull
    @Override
    public String toString() {
        return "Vehicle{" +
                "status=" + status +
                ", comment='" + comment + '\'' +
                ", info=" + info +
                '}';
    }
}
