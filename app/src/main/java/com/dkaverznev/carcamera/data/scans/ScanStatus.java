package com.dkaverznev.carcamera.data.scans;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.utils.ScanStatusUtils;

public class ScanStatus {

    public enum ScanStatusEnum {
        SUCCESS,
        BLOCK,
        WARNING,
        NOT_FOUND
    }

    private final ScanStatusEnum currentStatus;

    public ScanStatus() {
        this.currentStatus = ScanStatusEnum.NOT_FOUND;
    }

    public ScanStatus(ScanStatusEnum status) {
        this.currentStatus = status;
    }

    public ScanStatusEnum getScanStatusEnum() {
        return currentStatus;
    }


    public int getIntDescription() {
        if (currentStatus == null) {
            return R.string.scan_status_unknown;
        }
        Integer statusResId = ScanStatusUtils.FORMATED_DESCRIPTION_MAP.get(currentStatus);
        return statusResId == null ? R.string.scan_status_unknown : statusResId;
    }

    public String getStringFirebase() {
        if (currentStatus == null) {
            return "none";
        }
        switch (currentStatus) {
            case SUCCESS:
                return "success";
            case BLOCK:
                return "block";
            case WARNING:
                return "warning";
            case NOT_FOUND:
                return "not_found";
            default:
                return "none";
        }
    }
}