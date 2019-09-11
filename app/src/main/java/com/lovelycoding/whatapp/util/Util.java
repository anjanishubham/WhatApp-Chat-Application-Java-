package com.lovelycoding.whatapp.util;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.lovelycoding.whatapp.R;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Util {
    public static boolean verifyEmailId(String email)
    {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);

    }
    public static String dateConversion(String milliSecond){
        String date="";
        long sec=Long.valueOf(milliSecond);
        SimpleDateFormat timeFormat= new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat1= new SimpleDateFormat("dd MMMM yyyy");
        SimpleDateFormat dateFormat2= new SimpleDateFormat("dd MM yyyy");

        Date todayDate = new Date();

        Date da=new Date(sec);
        date=timeFormat.format(da);
        return date;
    }



}
