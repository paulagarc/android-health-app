package com.example.ehealth_projectg1.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ehealth_projectg1.model.repository.FirebaseRepository;

public class RecoverPasswordViewModel extends ViewModel {

    //repository
    private FirebaseRepository firebaseRepository;

    public RecoverPasswordViewModel(){
        //Init relation with firebase repository
        this.firebaseRepository = new FirebaseRepository();
    }

    public LiveData<String> getRecoverPassword(){
        return firebaseRepository.getRecoverPassword();
    }

    //-------------------- INSIDE APP CHANGE PASSWORD -------------------

    public void changePassword(String username, String oldPassword, String newPassword){
        firebaseRepository.changePassword(username, oldPassword, newPassword);
    }

    //-------------------- EMAIL RECOVER PASSWORD -------------------

    public void emailChangePassword(String email){
        firebaseRepository.emailChangePassword(email);
    }
}
