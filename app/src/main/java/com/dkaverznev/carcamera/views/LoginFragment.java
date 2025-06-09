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
import com.dkaverznev.carcamera.databinding.FragmentLoginBinding;
import com.dkaverznev.carcamera.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;
    private FragmentLoginBinding binding;
    private NavController navController;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        navController = Navigation.findNavController(view);

        initUI();
        setupObservers();
    }

    private void initUI() {
        binding.buttonBack.setOnClickListener(v ->
                navController.navigateUp());

        binding.buttonLogin.setOnClickListener(v -> {
            String userName = binding.editTextEmail.getText().toString();
            String password = binding.editTextPassword.getText().toString();
            binding.cardError.setVisibility(View.GONE);
            mViewModel.login(userName, password);
        });

        binding.textViewRegister.setOnClickListener(v -> {
            binding.cardError.setVisibility(View.GONE);
            navController.navigate(R.id.action_loginFragment_to_registerFragment);
        });
    }

    private void setupObservers() {
        mViewModel.loginSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success) {
                binding.cardError.setVisibility(View.GONE);
                navController.navigate(R.id.action_loginFragment_to_homeFragment);
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