package com.dkaverznev.carcamera.views;

import android.os.Bundle;
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
import com.dkaverznev.carcamera.databinding.FragmentResultBinding;
import com.dkaverznev.carcamera.utils.LicensePlateStringUtils;
import com.dkaverznev.carcamera.viewmodel.ResultViewModel;

public class ResultFragment extends Fragment {

    private ResultViewModel mViewModel;
    private FragmentResultBinding binding;
    private NavController navController;

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
        String licensePlate = args.getVehiclesLicense();

        binding.fabRefresh.setOnClickListener(v->{
            navController.navigate(R.id.action_resultFragment_to_homeFragment);
        });

        LicensePlateStringUtils.LicensePlateParts parts = LicensePlateStringUtils.splitLicensePlate(licensePlate);

        binding.textLicensePlate.setText(parts.numberPart);
        binding.textRegion.setText(parts.regionPart != null ? parts.regionPart : "");

        mViewModel.checkVehicleExists(licensePlate);

        binding.textRegion.setVisibility(View.GONE);
        binding.textRus.setVisibility(View.GONE);
        binding.imageFlag.setVisibility(View.GONE);
        binding.textInfo.setText("");
    }

    private void setupObservers() {
        mViewModel.vehicleData.observe(getViewLifecycleOwner(), vehicle -> {
            if (vehicle != null) {
                binding.textInfo.setText(vehicle.toString());
            } else {
                binding.textInfo.setText("Данные автомобиля не найдены.");
            }
        });

        mViewModel.vehicleExists.observe(getViewLifecycleOwner(), exists -> {
            if (exists != null) {
                if (exists) {
                    String licensePlate = ResultFragmentArgs.fromBundle(requireArguments()).getVehiclesLicense();
                    mViewModel.getVehicleDataByNumber(licensePlate);

                    binding.textRegion.setVisibility(View.VISIBLE);
                    binding.textRus.setVisibility(View.VISIBLE);
                    binding.imageFlag.setVisibility(View.VISIBLE);
                } else {
                    binding.textInfo.setText(getString(R.string.text_vehicle_not_found));
                    binding.textRegion.setVisibility(View.GONE);
                    binding.textRus.setVisibility(View.GONE);
                    binding.imageFlag.setVisibility(View.GONE);
                }
            }
        });

        mViewModel.databaseErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.textInfo.setText(errorMessage);
                binding.textRegion.setVisibility(View.GONE);
                binding.textRus.setVisibility(View.GONE);
                binding.imageFlag.setVisibility(View.GONE);
            }
        });

        mViewModel.authErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.textInfo.setText(errorMessage);
                binding.textRegion.setVisibility(View.GONE);
                binding.textRus.setVisibility(View.GONE);
                binding.imageFlag.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
