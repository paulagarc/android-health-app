package com.example.ehealth_projectg1.model.data;

public class Day {

    //class to create a new day medication
    private String day, medName;
    private int taken;

    public Day (String day, String medName, int taken){
        this.day = day;
        this.medName = medName;
        this.taken = taken;
    }

    public String getDay(){ return day;}

    public String getMedName(){ return medName;}

    public int getTaken(){ return taken;}

}
