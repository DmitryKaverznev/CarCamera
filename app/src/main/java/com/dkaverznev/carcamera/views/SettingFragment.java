package com.dkaverznev.carcamera.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dkaverznev.carcamera.databinding.FragmentBeginBinding;
import com.dkaverznev.carcamera.databinding.FragmentSettingsBinding;
import com.dkaverznev.carcamera.viewmodel.BeginViewModel;
import com.dkaverznev.carcamera.viewmodel.SettingViewModel;

public class SettingFragment extends Fragment {

    private SettingViewModel mViewModel;
    private FragmentSettingsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        initUI();
        setupObservers();
    }

    private void initUI() {

    }

    private void setupObservers() {
        // TODO
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}