package com.dkaverznev.carcamera.views.adapter;

import androidx.recyclerview.widget.DiffUtil;
import com.dkaverznev.carcamera.data.scans.Scan;
import java.util.List;
import java.util.Objects;

public class ScanDiffCallback extends DiffUtil.Callback {

    private final List<Scan> oldList;
    private final List<Scan> newList;

    public ScanDiffCallback(List<Scan> oldList, List<Scan> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        String oldLicense = oldList.get(oldItemPosition).getVehicleLicense();
        String newLicense = newList.get(newItemPosition).getVehicleLicense();
        return Objects.equals(oldLicense, newLicense);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

        Scan oldScan = oldList.get(oldItemPosition);
        Scan newScan = newList.get(newItemPosition);

        return Objects.equals(oldScan.getTime(), newScan.getTime()) &&
                Objects.equals(oldScan.getStatus().getStringFirebase(), newScan.getStatus().getStringFirebase());
    }
}