package com.example.ehealth_projectg1.model.data;

public class UserItem {

    //class to create a new user
    private String name;
    private String email;
    private String password;
    private int hardware;

    public UserItem(String name, String email, String password, int hardware){
        this.name = name;
        this.email = email;
        this.hardware = hardware;
        this.password = password;
    }

    //Password is not needed in UserItem class as it works with mAuth authentification
    public UserItem(String name, String email, int hardware){
        this.name = name;
        this.email = email;
        this.hardware = hardware;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public int getHardware() {
        return hardware;
    }
}
