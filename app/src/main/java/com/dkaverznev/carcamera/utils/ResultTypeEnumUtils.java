package com.dkaverznev.carcamera.utils;

import android.content.Context;
import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.data.vehicles.ResultTypeEnum;

import java.util.Map;

public class ResultTypeEnumUtils {
    private static final Map<ResultTypeEnum, Integer> TYPE_ENUM = Map.of(
            ResultTypeEnum.FORWARD, R.string.button_description_arrowForward,
            ResultTypeEnum.BACK, R.string.button_description_arrowForward_back
    );

    public static String getText(Context context, ResultTypeEnum status) {
        Integer resourceId = TYPE_ENUM.get(status);
        return context.getString(resourceId != null ? resourceId : R.string.button_description_arrowForward);
    }
}