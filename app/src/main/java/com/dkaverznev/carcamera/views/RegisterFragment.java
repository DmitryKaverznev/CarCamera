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
import com.dkaverznev.carcamera.databinding.FragmentRegisterBinding;
import com.dkaverznev.carcamera.viewmodel.RegisterViewModel;

public class RegisterFragment extends Fragment {

    private RegisterViewModel mViewModel;
    private FragmentRegisterBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        navController = Navigation.findNavController(view);

        initUI();
        setupObservers();
    }

    private void initUI() {

        binding.buttonBack.setOnClickListener(v ->
                navController.navigateUp());

        binding.buttonRegister.setOnClickListener(v -> {
            String userName = binding.editTextUsername.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            String confirmPassword = binding.editTextConfirmPassword.getText().toString();
            mViewModel.register(userName, password, confirmPassword);
        });

        binding.textViewLogin.setOnClickListener(v->
                navController.navigate(R.id.action_registerFragment_to_loginFragment));
    }

    private void setupObservers() {
        mViewModel.registerSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                navController.navigate(R.id.action_registerFragment_to_homeFragment);
            }
        });

        mViewModel.errorMessage.observe(getViewLifecycleOwner(), messageText -> {
            if (messageText != null && !messageText.isEmpty()) {
                binding.cardError.setVisibility(View.VISIBLE);
                binding.textError.setText(messageText);
            } else {
                binding.cardError.setVisibility(View.GONE);
                binding.textError.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}