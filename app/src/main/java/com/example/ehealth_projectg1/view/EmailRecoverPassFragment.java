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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ehealth_projectg1.R;
import com.example.ehealth_projectg1.viewmodel.RecoverPasswordViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class EmailRecoverPassFragment extends Fragment {

    private RecoverPasswordViewModel viewModel;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_emailrecoverpass, container, false);
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
        return etText.getText().toString().length() <= 0;
    }

    private void bindViews(final View view) {

        final EditText etEmail = view.findViewById(R.id.et_recovEmail);

        Button btnRecoverPassword = view.findViewById(R.id.btn_recoverpass);
        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(etEmail)){
                    Snackbar.make(Objects.requireNonNull(getView()), "The email needs to be introduced", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    String email = etEmail.getText().toString();
                    viewModel.emailChangePassword(email);
                }
            }
        });
    }

    private Observer<String> changePassObserver = new Observer<String>() {

        @Override
        public void onChanged(String taskMessage) {
            String[] parts = taskMessage.split("/");
            String task = parts[0];
            switch (task) {
                case "0":
                    Snackbar.make(Objects.requireNonNull(getView()), "The email introduced doesn't exists", Snackbar.LENGTH_SHORT).show();
                    break;
                case "1":
                    Snackbar.make(Objects.requireNonNull(getView()), "Email send", Snackbar.LENGTH_SHORT).show();
                    navController.navigate(R.id.action_emailRecoverPassFragment_to_loginFragment);
                    break;
            }
        }
    };

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(RecoverPasswordViewModel.class);
        viewModel.getRecoverPassword().observe(getViewLifecycleOwner(), changePassObserver);
    }

}
