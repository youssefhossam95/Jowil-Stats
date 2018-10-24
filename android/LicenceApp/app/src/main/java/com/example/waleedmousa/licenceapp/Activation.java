package com.example.waleedmousa.licenceapp;

import java.text.DateFormat;
import java.util.Date;

//
//@Entity
public class Activation {
    String activationKey ;

    public String getActivationKey() {
        return activationKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getActivationDate() {
        return activationDate;
    }

    String userName ;
    String activationDate ;

    Activation(String activationKey , String userName){
        this.activationKey  = activationKey ;
        this.userName = userName ;
        activationDate = DateFormat.getDateTimeInstance().format(new Date()) ;
    }

}
