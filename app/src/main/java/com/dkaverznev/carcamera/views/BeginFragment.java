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
import com.dkaverznev.carcamera.databinding.FragmentBeginBinding;
import com.dkaverznev.carcamera.viewmodel.BeginViewModel;

public class BeginFragment extends Fragment {

    private BeginViewModel mViewModel;
    private FragmentBeginBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentBeginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(BeginViewModel.class);
        navController = Navigation.findNavController(view);

        initUI();
        setupObservers();
    }

    private void initUI() {
        binding.buttonGoLogin.setOnClickListener(v -> {
            navController.navigate(R.id.action_beginFragment_to_loginFragment);
        });

        binding.buttonGoRegister.setOnClickListener(v -> {
            navController.navigate(R.id.action_beginFragment_to_registerFragment);
        });

        binding.buttonSettings.setOnClickListener(v -> {
            navController.navigate(R.id.action_beginFragment_to_settingFragment);
        });
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
