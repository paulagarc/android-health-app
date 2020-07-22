package com.example.ehealth_projectg1.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ehealth_projectg1.R;
import com.example.ehealth_projectg1.viewmodel.DisplayViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class DisplayParamFragment extends Fragment {

    private DisplayViewModel viewModel;

    private ImageView ivFall;
    private TextView tvWater;
    private TextView tvTemp;

    private static final String PERCENTAGE = " %";
    private static final String TEMPERATURE = " ºC";
    private static final String FAILED = "FAILED";
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_display, container, false);
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

    private void bindViews(final View view) {
        ivFall = view.findViewById(R.id.iv_fall);
        tvWater = view.findViewById(R.id.tv_water);
        tvTemp = view.findViewById(R.id.tv_temp);

        ImageView ivMoreWater = view.findViewById(R.id.iv_more_water);
        ivMoreWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int actionTap = 1; //Add water

                //Get actual value
                String tapStage = tvWater.getText().toString();
                tapStage = tapStage.substring(0, tapStage.length()-2);
                int tapStageInt = Integer.parseInt(tapStage);

                //Pass value to firebase
                viewModel.waterQuantity(actionTap, tapStageInt, getContext());
            }

        });

        ImageView ivLessWater = view.findViewById(R.id.iv_less_water);
        ivLessWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int actionTap = 2; //Less water

                //Get actual value
                String tapStage = tvWater.getText().toString();
                tapStage = tapStage.substring(0, tapStage.length()-2);
                int tapStageInt = Integer.parseInt(tapStage);

                //Pass value to firebase
                viewModel.waterQuantity(actionTap, tapStageInt, getContext());

            }
        });
    }

    private Observer<String> waterObservable = new Observer<String>() {

        @Override
        public void onChanged(String postParameters) {
        if (Objects.equals(postParameters, FAILED)) {
            //If failed, database acces fail
            Snackbar.make(Objects.requireNonNull(getView()), "There is a problem with the lecture of the sensors", Snackbar.LENGTH_SHORT).show();
        } else {
            //else -> correct database acces... printing in tvWater the waterAmount
            String text = postParameters + PERCENTAGE;
            tvWater.setText(text);
        }
        }
    };

    private Observer<String> tempObserver = new Observer<String>() {

        @Override
        public void onChanged(String postParameters) {
        if (postParameters.equals(FAILED)) {
            //postParameters == FAILED -> no acces bbdd
            Snackbar.make(Objects.requireNonNull(getView()), "There is a problem with the lecture of the sensors", Snackbar.LENGTH_SHORT).show();
        } else {
            //Correct case
            String text = postParameters + TEMPERATURE;
            tvTemp.setText(text);
        }
        }

    };


    private Observer<String> fallenObserver = new Observer<String>() {

        @Override
        public void onChanged(String postParameters) {
            if (Objects.equals(postParameters, TRUE)){
                //Set image to show the user as fallen
                ivFall.setImageResource(R.drawable.fall);

                //Inform that the user has fallen
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Alert");
                builder.setMessage("The user has fallen!");
                builder.setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton("Ok",  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });

                //Create and show alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else if(Objects.equals(postParameters, FALSE)) {
                //The user has not fallen , set image to presence
                ivFall.setImageResource(R.drawable.presence);
            } else {
                //null or FAILED connection -> no access bbdd
                Snackbar.make(Objects.requireNonNull(getView()), "There is a problem with the lecture of the sensors", Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    private void initViewModel() {
        //get the parameters values from Firebase
        viewModel = new ViewModelProvider(this).get(DisplayViewModel.class);
        viewModel.getFallen(getContext()).observe(getViewLifecycleOwner(), fallenObserver);
        viewModel.getTemperature(getContext()).observe(getViewLifecycleOwner(), tempObserver);
        viewModel.getWater(getContext()).observe(getViewLifecycleOwner(), waterObservable);
        viewModel.setHTTPToken(getContext());
    }
}
