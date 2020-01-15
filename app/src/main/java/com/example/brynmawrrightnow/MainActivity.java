package com.example.brynmawrrightnow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
// Mon: 0, Tue: 1, Wed: 2, Thur: 3, Fri: 4, Sat: 5, Sun: 6

public class MainActivity extends AppCompatActivity {
    private static final String PM = "pm";
    private static final String AM = "am";
    private static final int twelve = 12;
    private static final String midnight = "00:00";
    ArrayList<ArrayList<String>> uncommon = new ArrayList<>();
    ArrayList<ArrayList<String>> bookstore = new ArrayList<>();
    ArrayList<ArrayList<String>> canaday = new ArrayList<>();
    ArrayList<ArrayList<String>> carpenter = new ArrayList<>();
    ArrayList<ArrayList<String>> collier = new ArrayList<>();
    ArrayList<ArrayList<String>> erdman = new ArrayList<>();
    ArrayList<ArrayList<String>> newdorm = new ArrayList<>();
    ArrayList<ArrayList<String>> gym = new ArrayList<>();

    //private static final String TAG = "TIME NOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uncommon = newSchedule("uncommon.txt");
        bookstore = newSchedule("Bookstore.txt");
        canaday = newSchedule("Canaday.txt");
        carpenter = newSchedule("Carpenter.txt");
        collier = newSchedule("Collier.txt");
        erdman = newSchedule("Erdman.txt");
        newdorm = newSchedule("Newdorm.txt");
        gym = newSchedule("gym.txt");
        changeTimeText(null);
    }

    private ArrayList<ArrayList<String>> newSchedule (String filename){
        AssetManager assetManager = getAssets();
        ArrayList<ArrayList<String>> thisSchedule = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            ArrayList<String> newArray = new ArrayList<>();
            thisSchedule.add(newArray);
        }
        try {
            InputStream inputStream = assetManager.open(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            in.readLine();
            int counter = 0;
            while((line = in.readLine()) != null) {
                String[] tokens = line.split("\\s", 0);
                for (int i = 1; i<tokens.length; i++){
                    String thisTime = tokens[i];
                    if(thisTime.compareTo("closed") != 0){ // if closed, don't add anything
                        // else
                        // for each token convert into 24 time
                        // take first characters until ":", convert into integer
                        String subs = thisTime.substring(0,thisTime.indexOf(":"));
                        int hour = Integer.parseInt(subs);
                        // check if the last two characters are "pm" AND it's not "12pm"
                        String ampm = thisTime.substring(thisTime.length()-2, thisTime.length());
                        if(ampm.compareTo(PM)==0 && hour != twelve){
                            hour += twelve; // if so, add 12 to value
                        } else if (ampm.compareTo(AM) == 0 && hour == twelve){
                            hour = 0; // midnight is later than 23:00
                        }
                        // turn back into string formatted "14:15" or such
                        String hourString = "" + hour;
                        if (hourString.length() == 1){
                            hourString = "0" + hourString;
                        }
                        String finalTime = hourString + thisTime.substring(thisTime.indexOf(":"), thisTime.length()-2);
                        thisSchedule.get(counter).add(finalTime);
                    }
                }
                counter++;
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        //System.out.println("schedule formatted: ");
        //for(int i = 0; i < thisSchedule.size(); i++){
        //    System.out.println(thisSchedule.get(i));
        //}
        return thisSchedule;
    }

    // return true if time is within the right hours
    public boolean isOpen(String time, ArrayList<ArrayList<String>> schedule){
        System.out.println("schedule formatted: ");
        for(int i = 0; i < schedule.size(); i++){
            System.out.println(schedule.get(i));
        }
        String[] timeDetails = time.split("\\s", 0);
        int day = Integer.parseInt(timeDetails[0]);
        String timeOfDay = timeDetails[1];
        //System.out.println("timeOfDay: "+ timeOfDay);
        ArrayList<String> thisDayTimes = schedule.get(day);
        // for each pair of open-closed times, find if open or not
        for(int i = 0; i < thisDayTimes.size(); i = i+2){
            if(timeOfDay.compareTo(thisDayTimes.get(i)) >= 0 &&
                    (timeOfDay.compareTo(thisDayTimes.get(i+1)) < 0
                            || thisDayTimes.get(i+1).compareTo(midnight) == 0)){
                System.out.println("open!");
                return true;
            }
        }
        System.out.println("not open!");
        return false;
    }

    // return next time the item opens/closes
    public String nextTime(boolean status, String time, ArrayList<ArrayList<String>> schedule){
        if(status){ // if the item is open

        } else { // if it is closed

        }
        return "";
    }

    //function to change text according to when it closes or when it opens again
    public void changeTimeText(View view){
        String ourTime = timeNow();
        //System.out.println(ourTime);
        boolean openUncommon = isOpen(ourTime, uncommon);
        boolean openBookstore = isOpen(ourTime, bookstore);
        boolean openCanaday = isOpen(ourTime, canaday);
        boolean openCarpenter = isOpen(ourTime, carpenter);
        boolean openCollier = isOpen(ourTime, collier);
        boolean openErdman = isOpen(ourTime, erdman);
        boolean openNewdorm = isOpen(ourTime, newdorm);
        boolean openGym = isOpen(ourTime, gym);
        // if booleans are true, edit the text
    }

    private String timeNow(){ // work for later: we only want the day of the week and time
        //long timeInt = System.currentTimeMillis();
        //java.util.Date date = new java.util.Date(timeInt);
        //System.out.println("current time: ");
        //System.out.println(date);

        String result;
        Calendar rightNow = Calendar.getInstance();
        int day_of_week = -1;
        switch (rightNow.get(Calendar.DAY_OF_WEEK)) { //Calendar Sun 1, Mon 2, ..., Sat 7
            case 1:  day_of_week = 6; //1: Sunday
                break;
            case 2:  day_of_week = 0; //2: Monday
                break;
            case 3:  day_of_week = 1; //3: Tue
                break;
            case 4:  day_of_week = 2; //4: Wed
                break;
            case 5:  day_of_week = 3; //5: Thur
                break;
            case 6:  day_of_week = 4; //6: Fri
                break;
            case 7:  day_of_week = 5; //7: Sat
                break;
        }
        //Log.i(TAG, ""+rightNow.DAY_OF_WEEK);
        String hour = Integer.toString(rightNow.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(rightNow.get(Calendar.MINUTE));
        if(hour.length()==1) hour = "0"+hour;
        if(minute.length() ==1) minute = "0"+minute;
        result = day_of_week + " " + hour + ":" + minute;
        //Log.i(TAG, result);
        return result;
        // writes String in format Tues 14:45 -> "1 14:45"
    }
}