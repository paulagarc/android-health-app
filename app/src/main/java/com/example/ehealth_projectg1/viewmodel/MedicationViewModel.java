package com.example.ehealth_projectg1.viewmodel;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.ehealth_projectg1.model.data.Day;
import com.example.ehealth_projectg1.model.repository.FirebaseRepository;

import java.util.ArrayList;

public class MedicationViewModel extends ViewModel {

    //repository
    private FirebaseRepository firebaseRepository;

    public MedicationViewModel(){
        //Init relation with firebase repository
        this.firebaseRepository = new FirebaseRepository();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Day[]> getMedication(Context context){
        return firebaseRepository.getMedication(context);
    }

    public LiveData<Integer> getAddOkMedication(){
        return firebaseRepository.getAddOkMedication();
    }

    //RequiresApi needed for DayOfWeek class implementation
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addMedicationToCalendar(ArrayList<String> weekMedication, Context context){
        firebaseRepository.addMedicationToCalendar(weekMedication, context);
    }

}
