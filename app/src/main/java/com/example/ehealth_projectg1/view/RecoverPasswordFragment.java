package com.example.ehealth_projectg1.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ehealth_projectg1.R;
import com.example.ehealth_projectg1.viewmodel.RecoverPasswordViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class RecoverPasswordFragment extends Fragment {

    private RecoverPasswordViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_recoverpassword, container, false);
        //bind views
        bindViews(v);
        return v;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //init viewModel
        initViewModel();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private boolean isEmpty(EditText etText) {
        //to check if editText it's an empty string
        return etText.getText().toString().trim().length() <= 0;
    }

    private void bindViews(final View view) {

        final EditText etEmail = view.findViewById(R.id.et_email);
        final EditText etPassword = view.findViewById(R.id.et_newpass);
        final EditText etOldPassword = view.findViewById(R.id.et_oldpassword);

        Button btnRecoverPassword = view.findViewById(R.id.btn_recoverpass);
        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(etEmail) || (isEmpty(etPassword)) || (isEmpty(etOldPassword)) ){
                    Snackbar.make(Objects.requireNonNull(getView()), "All the parameters need to be fulfilled", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    String email = etEmail.getText().toString();
                    String newPassword = etPassword.getText().toString();
                    String oldPassword = etOldPassword.getText().toString();
                    viewModel.changePassword(email, oldPassword, newPassword);
                }
            }
        });
    }

    private Observer<String> changePassObserver = new Observer<String>() {

        @Override
        public void onChanged(String taskMessage) {
            String[] parts = taskMessage.split("/");
            String task = parts[0];
            String cause;
            switch (task){
                case "1":
                    //password changed correctly
                    Snackbar.make(Objects.requireNonNull(getView()), "Password changed correctly, you need to log in", Snackbar.LENGTH_LONG).show();
                    Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.nav_visualize,
                            new LoginFragment()).commit();
                    break;
                case "2":
                    //in cause we read and show the problem that firebase notify
                    //2 cases are when problems are relacionated with newPassword
                    cause = parts[1];
                    Snackbar.make(Objects.requireNonNull(getView()), cause, Snackbar.LENGTH_SHORT).show();
                    break;
                case "3":
                    //in cause we read and show the problem that firebase notify
                    //3 cases are when problems are relacionated with email and oldpassword
                    cause = parts[1];
                    Snackbar.make(Objects.requireNonNull(getView()), cause, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(RecoverPasswordViewModel.class);
        viewModel.getRecoverPassword().observe(getViewLifecycleOwner(), changePassObserver);
    }
}
