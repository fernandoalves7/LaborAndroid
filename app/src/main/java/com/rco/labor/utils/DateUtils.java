    package com.rco.labor.utils;

import java.util.Date;

/**
 * Created by Fernando on 10/28/2018.
 */

public class DateUtils {
    public static String getYyyyMmDdStr(Date d) {
        int month = d.getMonth() + 1;
        String mm = month < 10 ? "0" + month : "" + month;

        int day = d.getDate() + 1;
        String dd = day < 10 ? "0" + day : "" + day;

        return (d.getYear() + 1900) + "-" + mm + "-" + dd;
    }

    public static String getMmDdYyyyStr(Date d) {
        int month = d.getMonth() + 1;
        String mm = month < 10 ? "0" + month : "" + month;

        int day = d.getDate() + 1;
        String dd = day < 10 ? "0" + day : "" + day;

        return mm + "-" + dd + "-" + (d.getYear() + 1900);
    }

    public static String getHhmmssStr(Date d) {
        String hrs = d.getHours() < 10 ? "0" + d.getHours() : "" + d.getHours();
        String mins = d.getMinutes() < 10 ? "0" + d.getMinutes() : "" + d.getMinutes();
        String secs = d.getSeconds() < 10 ? "0" + d.getSeconds() : "" + d.getSeconds();

        return hrs + ":" + mins + ":" + secs;
    }

    public static String getYyyyMmDdHhmmssStr(Date d) {
        int month = d.getMonth() + 1;
        String mm = month < 10 ? "0" + month : "" + month;

        int day = d.getDate() + 1;
        String dd = day < 10 ? "0" + day : "" + day;

        String hrs = d.getHours() < 10 ? "0" + d.getHours() : "" + d.getHours();
        String mins = d.getMinutes() < 10 ? "0" + d.getMinutes() : "" + d.getMinutes();
        String secs = d.getSeconds() < 10 ? "0" + d.getSeconds() : "" + d.getSeconds();

        return (d.getYear() + 1900) + "-" + mm + "-" + dd + "T" + hrs + ":" + mins + ":" + secs + "Z";
    }

    public static String getMmDdYyyyHmmssStr(Date d) {
        int month = d.getMonth() + 1;
        String mm = month < 10 ? "0" + month : "" + month;

        int day = d.getDate() + 1;
        String dd = day < 10 ? "0" + day : "" + day;

        String hrs = d.getHours() < 10 ? "0" + d.getHours() : "" + d.getHours();
        String mins = d.getMinutes() < 10 ? "0" + d.getMinutes() : "" + d.getMinutes();
        String secs = d.getSeconds() < 10 ? "0" + d.getSeconds() : "" + d.getSeconds();

        return mm + "-" + dd + "-" + (d.getYear() + 1900) + " " + hrs + ":" + mins + ":" + secs;
    }

    public static String getNowYyyyMmDdHhmmss() {
        Date d = new Date();

        int month = d.getMonth() + 1;
        String mm = month < 10 ? "0" + month : "" + month;

        int day = d.getDate() + 1;
        String dd = day < 10 ? "0" + day : "" + day;

        String hrs = d.getHours() < 10 ? "0" + d.getHours() : "" + d.getHours();
        String mins = d.getMinutes() < 10 ? "0" + d.getMinutes() : "" + d.getMinutes();
        String secs = d.getSeconds() < 10 ? "0" + d.getSeconds() : "" + d.getSeconds();

        return (d.getYear() + 1900) + "-" + mm + "-" + dd + "T" + hrs + ":" + mins + ":" + secs + "Z";
    }
}
