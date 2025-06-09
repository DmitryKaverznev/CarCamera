package com.dkaverznev.carcamera.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.databinding.FragmentHomeBinding;
import com.dkaverznev.carcamera.viewmodel.HomeViewModel;
import com.dkaverznev.carcamera.views.adapter.ScanAdapter;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private FragmentHomeBinding binding;
    private NavController navController;
    private ScanAdapter scanAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        navController = Navigation.findNavController(view);

        setupRecyclerView();
        initUI();
        setupObservers();
    }

    private void setupRecyclerView() {
        scanAdapter = new ScanAdapter(getContext());
        binding.recyclerViewScanHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewScanHistory.setAdapter(scanAdapter);

        scanAdapter.setOnItemClickListener(scan -> {
            Toast.makeText(getContext(), "Нажат скан: " + scan.getVehicleLicense(), Toast.LENGTH_SHORT).show();
        });
    }

    private void initUI() {
        binding.buttonSettings.setOnClickListener(v ->
                navController.navigate(R.id.action_homeFragment_to_settingsFragment));

        binding.fabScan.setOnClickListener(v -> {
            navController.navigate(R.id.action_homeFragment_to_scanFragment);
        });

        binding.fabRefresh.setOnClickListener(v -> {
            mViewModel.loadAllScans();
        });
    }

    private void setupObservers() {
        mViewModel.getAllScans().observe(getViewLifecycleOwner(), scans -> {
            if (scans != null) {
                scanAdapter.setScans(scans);
                if (scans.isEmpty()) {
                    Log.d("HomeFragment", "Нет записей сканирования.");
                } else {
                    Log.d("HomeFragment", "Загружено сканов: " + scans.size());
                }
            } else {
                Log.e("HomeFragment", "Список сканов null.");
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
                Log.e("HomeFragment", "Ошибка загрузки сканов: " + errorMessage);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}