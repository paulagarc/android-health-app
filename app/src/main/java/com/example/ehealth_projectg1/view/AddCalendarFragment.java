package com.example.ehealth_projectg1.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ehealth_projectg1.R;
import com.example.ehealth_projectg1.viewmodel.MedicationViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class AddCalendarFragment extends Fragment {

    private MedicationViewModel viewModel;

    private ArrayList<String> weekMedication = new ArrayList<>();

    private String[] arraySpinner = {"metadol", "metaspirina"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addcalendar, container, false);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void bindViews(final View view) {

        //Init array information for spinners
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()).getApplicationContext(),
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Set adapter for each spinner

        final Spinner spMonday = view.findViewById(R.id.sp_med_monday);
        spMonday.setAdapter(adapter);

        final Spinner spTuesday = view.findViewById(R.id.sp_med_tuesday);
        spTuesday.setAdapter(adapter);

        final Spinner spWednesday = view.findViewById(R.id.sp_med_wednes);
        spWednesday.setAdapter(adapter);

        final Spinner spThrusday = view.findViewById(R.id.sp_med_thrus);
        spThrusday.setAdapter(adapter);

        final Spinner spFriday = view.findViewById(R.id.sp_med_friday);
        spFriday.setAdapter(adapter);

        final Spinner spSaturday = view.findViewById(R.id.sp_med_sat);
        spSaturday.setAdapter(adapter);

        final Spinner spSunday = view.findViewById(R.id.sp_med_sun);
        spSunday.setAdapter(adapter);

        Button btnAdd = view.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Add information taken from view to ArrayList
                addToArrayList(spMonday, R.id.cb_nomed_monday, view);
                addToArrayList(spTuesday, R.id.cb_nomed_tuesday, view);
                addToArrayList(spWednesday, R.id.cb_nomed_wend, view);
                addToArrayList(spThrusday, R.id.cb_nomed_thrusday, view);
                addToArrayList(spFriday, R.id.cb_nomed_friday, view);
                addToArrayList(spSaturday, R.id.cb_nomed_sat, view);
                addToArrayList(spSunday, R.id.cb_nomed_sun, view);

                //Pass information taken to the firebase
                viewModel.addMedicationToCalendar(weekMedication, getContext());
            }
        });
    }

    private void addToArrayList(Spinner sp, int idCheckBox, View view){
        //Get if checkbox is checked
        final CheckBox cb = view.findViewById(idCheckBox);

        if (!cb.isChecked()){
            //Take item selected from spinner
            weekMedication.add(sp.getSelectedItem().toString());
        } else {
            //No medication selected
            weekMedication.add("");
        }
    }

    private Observer<Integer> calendObserver = new Observer<Integer>() {

        @Override
        public void onChanged(Integer integer) {

            if (integer == 1) {
                //Inform the user the information was introduced correctly
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Information");
                builder.setMessage("The medication has been correctly introduced!");
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("Ok",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction().replace(R.id.nav_visualize,
                                new MedicationFragment()).commit();
                    }
                });

                //Create and show alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            } else
                Snackbar.make(Objects.requireNonNull(getView()), "Could not load the information to the application", Snackbar.LENGTH_LONG).show();
        }
    };

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MedicationViewModel.class);
        viewModel.getAddOkMedication().observe(getViewLifecycleOwner(), calendObserver);
    }

}

