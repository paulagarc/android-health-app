package com.example.ehealth_projectg1.view;

import android.annotation.SuppressLint;
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
import com.example.ehealth_projectg1.model.data.UserItem;
import com.example.ehealth_projectg1.viewmodel.RegisterViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class RegisterFragment extends Fragment {

    private RegisterViewModel viewModel;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        //bind views
        bindViews(v);
        //init viewmodel
        initViewModel();
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

        final EditText etName = view.findViewById(R.id.et_name);
        final EditText etEmail = view.findViewById(R.id.et_email);
        final EditText etPassword = view.findViewById(R.id.et_password);
        final EditText etSerialNumber = view.findViewById(R.id.et_hardware);

        TextView tv_signup_login_hint = view.findViewById(R.id.tv_signup_login_hint);
        tv_signup_login_hint.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_registerFragment_to_loginFragment);
            }


        });

        Button btnSignUp = view.findViewById(R.id.btn_signup);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(etName) || (isEmpty(etEmail)) || isEmpty(etPassword) || (isEmpty(etSerialNumber))) {
                    Snackbar.make(Objects.requireNonNull(getView()), "All the parameters need to be fulfilled", Snackbar.LENGTH_SHORT).show();
                } else {
                    String name = etName.getText().toString();
                    String email = etEmail.getText().toString();
                    String password = etPassword.getText().toString();
                    int serialHarware = Integer.parseInt(etSerialNumber.getText().toString());
                    UserItem newUser = new UserItem(name, email, serialHarware);
                    viewModel.newRegister(newUser, password);
                }
            }
        });
    }

    private Observer<String> registerObservable = new Observer<String>() {

        @Override
        public void onChanged(String taskMessage) {

            String[] parts = taskMessage.split("/");
            String task = parts[0]; // 0-> User already exists ; 1 -> SIGN UP ; 2 -> addOnFirebaseFailure; 3-> Excepcion cause
            switch (task){
                case "0":
                    Snackbar.make(Objects.requireNonNull(getView()), "The user already exists", Snackbar.LENGTH_SHORT).show();
                    break;
                case "1":
                    Snackbar.make(Objects.requireNonNull(getView()), "Register succesfull, now you can sign in", Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_loginFragment);
                    break;
                case "2":
                    String cause = parts[1]; //in cause we read and show the problem that firebase notify
                    Snackbar.make(Objects.requireNonNull(getView()), cause, Snackbar.LENGTH_SHORT).show();
                    break;
                case "3":
                    Snackbar.make(Objects.requireNonNull(getView()), "There is a problem with our systems, try to sign up later", Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);
        viewModel.getRegister().observe(getViewLifecycleOwner(), registerObservable);
    }
}
