package com.dkaverznev.carcamera.utils;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.data.scans.ScanStatus;

import java.util.Map;

public class ScanStatusUtils {

    public static final Map<ScanStatus.ScanStatusEnum, Integer> FORMATED_DESCRIPTION_MAP;

    static {
        FORMATED_DESCRIPTION_MAP = Map.of(
                ScanStatus.ScanStatusEnum.SUCCESS,
                R.string.scan_status_success,
                ScanStatus.ScanStatusEnum.BLOCK,
                R.string.scan_status_block,
                ScanStatus.ScanStatusEnum.WARNING,
                R.string.scan_status_warning,
                ScanStatus.ScanStatusEnum.NOT_FOUND,
                R.string.scan_status_not_found);
    }
}