package com.example.ehealth_projectg1.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ehealth_projectg1.model.repository.FirebaseRepository;
import com.example.ehealth_projectg1.model.repository.HTTPMessagingFirebase;

public class DisplayViewModel extends ViewModel {

    //repository
    private FirebaseRepository firebaseRepository;
    private HTTPMessagingFirebase httpMessagingFirebase;

    public DisplayViewModel(){
        //Init relation with firebase repository
        this.firebaseRepository = new FirebaseRepository();
        this.httpMessagingFirebase = new HTTPMessagingFirebase();
    }

    public void waterQuantity(int actionTap, int tapStageInt, Context context){
        firebaseRepository.waterQuantity(actionTap, tapStageInt, context);
    }

    public LiveData<String> getWater(Context context){
        return firebaseRepository.getWater(context);
    }
    public LiveData<String> getFallen(Context context){
        return firebaseRepository.getFallen(context);
    }
    public LiveData<String> getTemperature(Context context) {
        return firebaseRepository.getTemperature(context);
    }

    public void setHTTPToken(Context context){
        httpMessagingFirebase.setHTTPMessagingToken(context);
    }
}
