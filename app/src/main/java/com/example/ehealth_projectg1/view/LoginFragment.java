package com.example.ehealth_projectg1.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ehealth_projectg1.R;
import com.example.ehealth_projectg1.viewmodel.LoginViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        //bind views
        bindViews(v);
        return v;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //init navcontroller
        initNavController();
        //init viewModel
        initViewModel();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void initNavController(){
        navController = Navigation.findNavController(Objects.requireNonNull(getView()));
    }

    private boolean isEmpty(EditText etText) {
        //to check if editText it's an empty string
        return etText.getText().toString().trim().length() <= 0;
    }

    private void bindViews(final View view) {

        final EditText etEmail = view.findViewById(R.id.etEmail);
        final EditText etPassword = view.findViewById(R.id.etPassword);

        TextView tvSignUp = view.findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_registerFragment);

            }

        });

        TextView tvRecoverPassword = view.findViewById(R.id.tvRecoverPassword);
        tvRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_emailRecoverPassFragment);
            }

        });

        Button btnLogIn = view.findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(etEmail) || (isEmpty(etPassword))) {
                    Snackbar.make(Objects.requireNonNull(getView()), "All parameters need to be filled", Snackbar.LENGTH_SHORT).show();
                } else {
                    String username = etEmail.getText().toString();
                    String password = etPassword.getText().toString();

                    viewModel.logIn(username, password, getContext());
                }
            }
        });
    }

    private Observer<String> logInObserver = new Observer<String>() {

        @Override
        public void onChanged(String taskMessage) {
        String[] parts = taskMessage.split("/");
        String task = parts[0]; // 1 -> LOG IN or 0 -> NO LOG IN
        switch (task){
            case "0":
                String cause = parts[1]; //in cause we read and show the problem that firebase notify
                Snackbar.make(Objects.requireNonNull(getView()), cause, Snackbar.LENGTH_SHORT).show();
                break;
            case "1":
                //Correct Login user and password -> Enter to the application
                Intent intent = new Intent(getActivity(), SupervisionActivity.class);
                startActivity(intent);
                break;
        }
        }

    };

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        viewModel.getLogIn().observe(getViewLifecycleOwner(), logInObserver);
    }

}
