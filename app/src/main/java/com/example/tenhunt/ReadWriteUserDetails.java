package com.example.tenhunt;

public class ReadWriteUserDetails {
    public String fullName, doB, mobile;

    //Constructor
    public ReadWriteUserDetails(){

    }

    public ReadWriteUserDetails(String textDoB, String textMobile){
        this.doB = textDoB;
        this.mobile = textMobile;
    }
}
