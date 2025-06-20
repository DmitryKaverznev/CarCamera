package com.dkaverznev.carcamera.data.scans;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import com.dkaverznev.carcamera.R;

public class Scan {
    @Exclude
    private String vehicleLicense;

    private ScanStatus status;
    private Timestamp time;
    private String type;

    public Scan() {
        this.status = new ScanStatus(ScanStatus.ScanStatusEnum.NOT_FOUND);
        this.time = Timestamp.now();
        this.type = null;
    }

    public Scan(ScanStatus status, Timestamp time, String type) {
        this.status = status;
        this.time = time;
        this.type = type;
    }

    public String getVehicleLicense() {
        return vehicleLicense;
    }

    public void setVehicleLicense(String vehicleLicense) {
        this.vehicleLicense = vehicleLicense;
    }

    @PropertyName("status")
    public String getStatusForFirebase() {
        if (status != null) {
            return status.getStringFirebase();
        }
        return "none";
    }

    @PropertyName("status")
    public void setStatusForFirebase(String statusString) {
        ScanStatus.ScanStatusEnum enumStatus;
        switch (statusString) {
            case "success":
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
        this.status = new ScanStatus(enumStatus);
    }

    @Exclude
    public ScanStatus getStatus() {
        return status;
    }

    public int getStatusDescriptionResId() {
        if (status != null) {
            return status.getIntDescription();
        }
        return R.string.scan_status_unknown;
    }

    @PropertyName("time")
    public Timestamp getTime() {
        return time;
    }

    // Геттер для type
    @PropertyName("type")
    public String getType() {
        return type;
    }

    // Сеттер для type
    @PropertyName("type")
    public void setType(String type) {
        this.type = type;
    }
}