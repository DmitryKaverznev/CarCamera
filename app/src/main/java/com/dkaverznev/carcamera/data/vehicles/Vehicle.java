package com.dkaverznev.carcamera.data.vehicles;

import com.dkaverznev.carcamera.data.scans.ScanStatus;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.util.Map;

public class Vehicle {
    public String note;
    @PropertyName("status")
    public String firebaseStatus;

    public Map<String, String> info;

    public Vehicle() {
    }

    public Vehicle(String note, String firebaseStatus, Map<String, String> info) {
        this.note = note;
        this.firebaseStatus = firebaseStatus;
        this.info = info;
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Map<String, String> getInfo() { return info; }
    public void setInfo(Map<String, String> info) { this.info = info; }


    @PropertyName("status")
    public String getFirebaseStatus() {
        return firebaseStatus;
    }

    @PropertyName("status")
    public void setFirebaseStatus(String firebaseStatus) {
        this.firebaseStatus = firebaseStatus;
    }

    @Exclude
    public ScanStatus getScanStatus() {
        ScanStatus.ScanStatusEnum enumStatus;
        if (firebaseStatus == null) {
            enumStatus = ScanStatus.ScanStatusEnum.NOT_FOUND;
        } else {
            switch (firebaseStatus) {
                case "allowed":
                    enumStatus = ScanStatus.ScanStatusEnum.SUCCESS;
                    break;
                case "block":
                    enumStatus = ScanStatus.ScanStatusEnum.BLOCK;
                    break;
                case "warning":
                    enumStatus = ScanStatus.ScanStatusEnum.WARNING;
                    break;
                default:
                    enumStatus = ScanStatus.ScanStatusEnum.NOT_FOUND;
                    break;
            }
        }
        return new ScanStatus(enumStatus);
    }
}