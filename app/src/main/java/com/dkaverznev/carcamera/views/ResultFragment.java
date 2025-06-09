package com.dkaverznev.carcamera.views;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.data.scans.Scan;
import com.dkaverznev.carcamera.data.scans.ScanStatus;
import com.dkaverznev.carcamera.databinding.FragmentResultBinding;
import com.dkaverznev.carcamera.utils.LicensePlateStringUtils;
import com.dkaverznev.carcamera.utils.ResultIconsUtils;
import com.dkaverznev.carcamera.viewmodel.ResultViewModel;
import com.dkaverznev.carcamera.data.vehicles.Vehicle;
import com.google.firebase.Timestamp; // Нужен для Timestamp.now()
import java.util.Map;
import java.util.Objects;

public class ResultFragment extends Fragment {

    private ResultViewModel mViewModel;
    private FragmentResultBinding binding;
    private NavController navController;
    private String currentLicensePlate;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        navController = Navigation.findNavController(view);

        initUI();
        setupObservers();
    }

    private void initUI() {
        ResultFragmentArgs args = ResultFragmentArgs.fromBundle(getArguments());
        currentLicensePlate = args.getVehiclesLicense();

        binding.fabRefresh.setOnClickListener(v ->
                navController.navigateUp());

        setLicensePlateText(currentLicensePlate);

        Log.e("ResultFragment", "License Plate: " + currentLicensePlate);

        mViewModel.getVehicleDataByNumber(currentLicensePlate);
        mViewModel.getLatestScan(currentLicensePlate);

        hideContent();
    }

    private void setupObservers() {
        mViewModel.vehicleData.observe(getViewLifecycleOwner(), vehicle -> {
            if (vehicle != null) {
                if (vehicle.getScanStatus().getScanStatusEnum() == ScanStatus.ScanStatusEnum.NOT_FOUND) {
                    hideContent();
                } else {
                    showContent();
                }

                binding.textNote.setText(vehicle.getNote());
                displayVehicleInfo(vehicle);


                Scan newScan = new Scan(vehicle.getScanStatus(), Timestamp.now());
                newScan.setVehicleLicense(currentLicensePlate);
                mViewModel.addScanRecord(newScan);

            } else {
                hideContent();

                Scan newScan = new Scan(new ScanStatus(ScanStatus.ScanStatusEnum.NOT_FOUND), Timestamp.now());
                newScan.setVehicleLicense(currentLicensePlate);
                mViewModel.addScanRecord(newScan);
            }
        });

        mViewModel.scanData.observe(getViewLifecycleOwner(), scan -> {
            if (scan != null) {
                binding.textInfoStatus.setText(getString(scan.getStatus().getIntDescription()));
                setStatusIcon(scan.getStatus().getScanStatusEnum());
            } else {
                binding.textInfoStatus.setText(getString(R.string.scan_status_not_found));
            }
        });

        mViewModel.databaseErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e("ResultFragment", "Ошибка базы данных: " + errorMessage);
            }
        });

        // Наблюдаем за сообщениями об ошибках аутентификации
        mViewModel.authErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e("ResultFragment", "Ошибка аутентификации: " + errorMessage);
            }
        });

        // Наблюдаем за сообщениями об ошибках из DataScansRepository
        mViewModel.scanErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e("ResultFragment", "Ошибка при работе со сканированием: " + errorMessage);
            }
        });
    }

    private void displayVehicleInfo(Vehicle vehicle) {
        Map<String, String> info = vehicle.getInfo();
        if (info != null && !info.isEmpty()) {
            StringBuilder infoBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : info.entrySet()) {
                infoBuilder.append("<b>").append(entry.getKey()).append(":</b> ")
                        .append(entry.getValue())
                        .append("<br>");
            }
            binding.textInfoContent.setText(Html.fromHtml(infoBuilder.toString(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            binding.textInfoContent.setText(R.string.scan_status_not_found);
        }
    }

    private void setLicensePlateText(String licensePlate) {
        LicensePlateStringUtils.LicensePlateParts parts = LicensePlateStringUtils.splitLicensePlate(licensePlate);
        binding.textLicensePlate.setText(Objects.requireNonNullElse(parts.numberPart, ""));
        binding.textRegion.setText(Objects.requireNonNullElse(parts.regionPart, ""));
    }

    private void showContent() {
        binding.hiddenContentContainer.setVisibility(View.VISIBLE);
    }

    private void hideContent() {
        binding.hiddenContentContainer.setVisibility(View.GONE);
    }

    private void setStatusIcon(ScanStatus.ScanStatusEnum status) {
        binding.iconStatusContainer.setBackground(ResultIconsUtils.getShape(requireContext(), status));
        binding.imageStatusIcon.setImageResource(ResultIconsUtils.getIcon(status));
        binding.imageStatusIcon.setColorFilter(ResultIconsUtils.getColor(requireContext(), status), PorterDuff.Mode.SRC_IN);
        binding.textInfoStatus.setTextColor(ResultIconsUtils.getColor(requireContext(), status));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewModel.vehicleData.removeObservers(getViewLifecycleOwner());
        mViewModel.scanData.removeObservers(getViewLifecycleOwner());
        mViewModel.databaseErrorMessage.removeObservers(getViewLifecycleOwner());
        mViewModel.authErrorMessage.removeObservers(getViewLifecycleOwner());
        mViewModel.scanErrorMessage.removeObservers(getViewLifecycleOwner());
        binding = null;
    }
}