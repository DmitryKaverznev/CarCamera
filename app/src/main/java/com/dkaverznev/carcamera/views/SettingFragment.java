package com.dkaverznev.carcamera.views;

import android.os.Bundle;
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

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.databinding.FragmentSettingsBinding;
import com.dkaverznev.carcamera.viewmodel.SettingViewModel;

public class SettingFragment extends Fragment {

    private SettingViewModel mViewModel;
    private FragmentSettingsBinding binding;
    private NavController navController;

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
        navController = Navigation.findNavController(view);

        initUI();
        setupObservers();
    }

    private void initUI() {
        binding.buttonBack.setOnClickListener(v -> {
            navController.navigateUp();
        });

        binding.buttonLogout.setOnClickListener(v -> {
            mViewModel.logOut();
            navController.navigate(R.id.beginFragment);
        });

        binding.buttonDelete.setOnClickListener(v -> {
            mViewModel.deleteData();
        });
    }

    private void setupObservers() {
        mViewModel.logoutSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                navController.navigate(R.id.loginFragment);
            }
        });

        mViewModel.errorMessageAuth.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), "Ошибка: " + message, Toast.LENGTH_LONG).show();
            }
        });

        mViewModel.firebaseUser.observe(getViewLifecycleOwner(), firebaseUser -> {
            if (firebaseUser != null) {
                binding.textViewUserEmail.setText(firebaseUser.getEmail());
            }
        });

        mViewModel.deleteSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Все данные успешно удалены", Toast.LENGTH_SHORT).show();
                    navController.navigate(R.id.loginFragment);
                } else {
                    Toast.makeText(getContext(), "Не удалось полностью удалить аккаунт и данные", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}