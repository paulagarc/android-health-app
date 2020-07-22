package com.example.ehealth_projectg1.view;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ehealth_projectg1.R;

import com.example.ehealth_projectg1.model.data.Day;
import com.example.ehealth_projectg1.viewmodel.MedicationViewModel;

import java.util.Objects;

public class MedicationFragment extends Fragment {

    private RecyclerView rvMedication;

    @Nullable
    @Override
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_medication, container, false);
        //bind views
        bindViews(v);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityCreated(@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        //init viewModel
        initViewModel();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void bindViews(final View view) {
        rvMedication = view.findViewById(R.id.rv_medication);

        //Use this setting to improve performance
        rvMedication.setHasFixedSize(true);

        //Use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rvMedication.setLayoutManager(linearLayoutManager);

        //Open AddCalendar Fragment
        ImageView ivAddCalendar = view.findViewById(R.id.iv_addcalendar);
        ivAddCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.nav_visualize,
                        new AddCalendarFragment()).commit();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Observer<Day[]> medObserver = new Observer<Day[]>() {

        @Override
        public void onChanged(Day[] days) {
            //Set information to the view adapter
            DayAdapter dayAdapter = new DayAdapter(days);
            rvMedication.setAdapter(dayAdapter);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initViewModel() {
        MedicationViewModel viewModel = new ViewModelProvider(this).get(MedicationViewModel.class);
        viewModel.getMedication(getContext()).observe(getViewLifecycleOwner(), medObserver);
    }
}
