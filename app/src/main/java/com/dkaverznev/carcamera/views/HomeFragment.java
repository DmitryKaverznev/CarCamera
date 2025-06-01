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
import com.dkaverznev.carcamera.databinding.FragmentHomeBinding;
import com.dkaverznev.carcamera.viewmodel.HomeViewModel;

public class HomeFragment extends Fragment {

    private HomeViewModel mViewModel;
    private FragmentHomeBinding binding;
    private NavController navController;


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

        initUI();
        setupObservers();
    }

    private void initUI() {
        binding.buttonSettings.setOnClickListener(v->
                navController.navigate(R.id.action_homeFragment_to_settingsFragment));

        binding.fabScan.setOnClickListener(v->{
            navController.navigate(R.id.action_homeFragment_to_scanFragment);
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