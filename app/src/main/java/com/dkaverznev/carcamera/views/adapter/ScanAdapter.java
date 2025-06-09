package com.dkaverznev.carcamera.views.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.dkaverznev.carcamera.data.scans.Scan;
import com.dkaverznev.carcamera.databinding.ItemScanHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ScanViewHolder> {

    private final List<Scan> scans = new ArrayList<>();
    private OnItemClickListener listener;
    private final Context context;

    public ScanAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(Scan scan);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setScans(List<Scan> newScans) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ScanDiffCallback(this.scans, newScans));

        this.scans.clear();
        this.scans.addAll(newScans);

        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ScanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemScanHistoryBinding binding = ItemScanHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ScanViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ScanViewHolder holder, int position) {
        Scan currentScan = scans.get(position);
        holder.bind(currentScan);
    }

    @Override
    public int getItemCount() {
        return scans.size();
    }

    public class ScanViewHolder extends RecyclerView.ViewHolder {

        private final ItemScanHistoryBinding binding;

        public ScanViewHolder(@NonNull ItemScanHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(scans.get(position));
                }
            });
        }

        public void bind(Scan scan) {
            if (scan.getVehicleLicense() != null && !scan.getVehicleLicense().isEmpty()) {
                binding.textLicensePlate.setText(scan.getVehicleLicense());
            } else {
                binding.textLicensePlate.setText("Номерной знак не указан");
            }

            if (scan.getTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String formattedTime = sdf.format(scan.getTime().toDate());
                String statusDescription = context.getString(scan.getStatusDescriptionResId());

                String text = statusDescription + "\n" + formattedTime;
                binding.textShortInfo.setText(text);
            } else {
                binding.textShortInfo.setText(context.getString(scan.getStatusDescriptionResId()));
            }
        }
    }
}