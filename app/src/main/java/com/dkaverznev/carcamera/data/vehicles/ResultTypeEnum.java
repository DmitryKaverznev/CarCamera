package com.dkaverznev.carcamera.data.vehicles;

import android.content.Context;

import androidx.annotation.NonNull;

import com.dkaverznev.carcamera.utils.ResultTypeEnumUtils;

public enum ResultTypeEnum {
    FORWARD,
    BACK;

    @NonNull
    public String toStringView(Context context) {
        return ResultTypeEnumUtils.getText(context, this);
    }

    @NonNull
    public String toStringFirebase() {
        return this.toString().toLowerCase();
    }
}