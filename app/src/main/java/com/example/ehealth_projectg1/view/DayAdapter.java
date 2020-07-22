package com.example.ehealth_projectg1.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ehealth_projectg1.R;
import com.example.ehealth_projectg1.model.data.Day;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.MyViewHolder> {

    private Day[] dayList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        //Init view
        private LinearLayout mTextView;
        private MyViewHolder(LinearLayout v) {
            super(v);
            mTextView = v;
        }
    }

    public DayAdapter(Day[] dayList) {
        //Set information for the adapter
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create blank fields for introducing information
        LinearLayout itemView = (LinearLayout) LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_day, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String NO_MED_TAKEN = "No medication taken";
        String MED_INCORRECT = "Incorrect medication taken";

        //Write information in fields
        final Day actualDayList = dayList[position];

        //Day
        TextView tvDay = holder.mTextView.findViewById(R.id.tv_day);
        tvDay.setText(actualDayList.getDay());

        //Medication
        TextView tvMed = holder.mTextView.findViewById(R.id.tv_med);
        TextView tvTaken = holder.mTextView.findViewById(R.id.tv_taken);

        if(actualDayList.getMedName().length() > 0){
            //Medication to take
            tvMed.setText(actualDayList.getMedName());

            //Set colors depending if medications is taken or not
            int taken = actualDayList.getTaken();
            if(taken == 1) {
                //Medication taken correctly
                tvMed.setTextColor(ContextCompat.getColor(holder.mTextView.getContext(), R.color.green));

            } else {
                //Medication not taken correctly
                tvMed.setTextColor(ContextCompat.getColor(holder.mTextView.getContext(), R.color.colorPrimaryDarkRed));

                if (taken == 0 && actualDayList.getMedName().length() > 0){
                    //No medication  taken
                    tvTaken.setTextColor(ContextCompat.getColor(holder.mTextView.getContext(), R.color.orange));
                    tvTaken.setText(NO_MED_TAKEN);

                } else if (taken == -1 && actualDayList.getMedName().length() > 0) {
                    //Other medication taken
                    tvTaken.setTextColor(ContextCompat.getColor(holder.mTextView.getContext(), R.color.colorPrimaryDarkRed));
                    tvTaken.setText(MED_INCORRECT);

                } else {
                    //Other cases
                    tvTaken.setVisibility(View.GONE);
                }
            }

        } else {
            //No medication to take
            tvMed.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dayList.length;
    }

}
