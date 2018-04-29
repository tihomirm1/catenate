package com.paraflow.mobilecrm;

import java.util.ArrayList;

/**
 * Created by tmihaylov on 20.2.2018 г..
 */

public class Options {


    public static  ArrayList<String> yes_no = new ArrayList();
    public static  ArrayList<String> meeting_status = new ArrayList();


    static {
        yes_no.add("Да");
        yes_no.add("Не");

        meeting_status.add("Планирана");
        meeting_status.add("Проведена");
    }




}
