package com.example.ehealth_projectg1.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ehealth_projectg1.model.data.UserItem;
import com.example.ehealth_projectg1.model.repository.FirebaseRepository;

public class RegisterViewModel extends ViewModel {

    //repository
    private FirebaseRepository firebaseRepository;

    public RegisterViewModel(){
        //Init relation with firebase repository
        this.firebaseRepository = new FirebaseRepository();
    }

    public void newRegister(UserItem newUser, String password){
        firebaseRepository.newRegister(newUser, password);
    }

    public LiveData<String> getRegister(){
        return firebaseRepository.getRegister();
    }
}
