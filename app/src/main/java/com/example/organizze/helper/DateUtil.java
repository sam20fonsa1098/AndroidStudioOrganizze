package com.example.organizze.helper;

import java.text.SimpleDateFormat;

public class DateUtil {
    public static String currentDate() {
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dateString  =simpleDateFormat.format(date);
        return dateString;
    }
    public static String convertToNumber(String data) {
        String array[] = data.split("/");
        return array[1] + array[2];
    }
}
