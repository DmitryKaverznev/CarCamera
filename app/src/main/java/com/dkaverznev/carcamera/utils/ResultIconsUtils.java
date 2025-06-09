package com.dkaverznev.carcamera.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.data.scans.ScanStatus;

import java.util.Map;

public class ResultIconsUtils {

    private static final Map<ScanStatus.ScanStatusEnum, Integer> RESULT_ICONS = Map.of(
            ScanStatus.ScanStatusEnum.SUCCESS, R.drawable.ic_check,
            ScanStatus.ScanStatusEnum.BLOCK, R.drawable.ic_block,
            ScanStatus.ScanStatusEnum.WARNING, R.drawable.ic_warning,
            ScanStatus.ScanStatusEnum.NOT_FOUND, R.drawable.ic_close
    );

    public static int getIcon(ScanStatus.ScanStatusEnum status) {
        Integer resourceIcon = RESULT_ICONS.get(status);
        return resourceIcon != null ? resourceIcon : R.drawable.ic_status_error;
    }


    private static final Map<ScanStatus.ScanStatusEnum, Integer> RESULT_SHAPE = Map.of(
            ScanStatus.ScanStatusEnum.SUCCESS, R.drawable.circle_background_green,
            ScanStatus.ScanStatusEnum.BLOCK, R.drawable.circle_background_red,
            ScanStatus.ScanStatusEnum.WARNING, R.drawable.circle_background_yellow,
            ScanStatus.ScanStatusEnum.NOT_FOUND, R.drawable.circle_background_orange
    );

    public static Drawable getShape(Context context, ScanStatus.ScanStatusEnum status) {
        Integer resourceId = RESULT_SHAPE.get(status);
        int finalResourceId = resourceId != null ? resourceId : R.drawable.circle_background_orange;
        return ContextCompat.getDrawable(context, finalResourceId);
    }


    private static final Map<ScanStatus.ScanStatusEnum, Integer> RESULT_COLOR = Map.of(
            ScanStatus.ScanStatusEnum.SUCCESS, R.color.green_status,
            ScanStatus.ScanStatusEnum.BLOCK, R.color.red_status,
            ScanStatus.ScanStatusEnum.WARNING, R.color.yellow_status,
            ScanStatus.ScanStatusEnum.NOT_FOUND, R.color.orange_status
    );

    public static int getColor(Context context, ScanStatus.ScanStatusEnum status) {
        Integer resourceId = RESULT_COLOR.get(status);
        int finalResourceId = resourceId != null ? resourceId : R.color.orange_status;
        return ContextCompat.getColor(context, finalResourceId);
    }
}