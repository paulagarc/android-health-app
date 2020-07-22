package com.example.ehealth_projectg1.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ehealth_projectg1.model.repository.FirebaseRepository;

public class LoginViewModel extends ViewModel {

    //repository
    private FirebaseRepository firebaseRepository;

    public LoginViewModel(){
        //Init relation with firebase repository
        this.firebaseRepository = new FirebaseRepository();
    }

    public void logIn(String username, String password, Context context){
       firebaseRepository.logIn(username, password, context);
    }

    public LiveData<String> getLogIn(){
        return firebaseRepository.getLogIn();
    }

}
