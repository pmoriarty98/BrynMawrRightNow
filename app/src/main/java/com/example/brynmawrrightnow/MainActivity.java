package com.example.brynmawrrightnow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
    ArrayList<ArrayList<String>> BMCtoHAV = new ArrayList<>();
    ArrayList<ArrayList<String>> HAVtoBMC = new ArrayList<>();

    //private static final String TAG = "TIME NOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }

    public void refresh(View view){
        setup();
    }

    private void setup() {
        ImageView img = (ImageView)findViewById(R.id.lantern); // get appropriate image
        Drawable myDrawable = getResources().getDrawable(R.drawable.lantern);
        img.setImageDrawable(myDrawable);

        uncommon = newSchedule("uncommon.txt");
        bookstore = newSchedule("Bookstore.txt");
        canaday = newSchedule("Canaday.txt");
        carpenter = newSchedule("Carpenter.txt");
        collier = newSchedule("Collier.txt");
        erdman = newSchedule("Erdman.txt");
        newdorm = newSchedule("Newdorm.txt");
        gym = newSchedule("gym.txt");
        BMCtoHAV = newScheduleBlueBus("BMCtoHC.txt");
        HAVtoBMC = newScheduleBlueBus("HCtoBMC.txt");

        // display the last time refreshed
        TextView displayTimeNow = (TextView) findViewById(R.id.currentTime);
        Date date = Calendar.getInstance().getTime();
        String timeText = "" + date;
        String[] datePieces = timeText.split("\\s",0);
        // take the first five characters and regularize
        String simplifiedTime = toRegularTime(datePieces[3].substring(0,5));
        System.out.println("simplifiedTime: " + simplifiedTime);
        String compiledDate = "The current time is:\n" + datePieces[0] + " at " + simplifiedTime;
        displayTimeNow.setText(compiledDate);
        changeTimeText(null);
    }

    //function to change text according to when it closes or when it opens again
    public void changeTimeText(View view){
        String ourTime = timeNow();
        //System.out.println(ourTime);
        ArrayList<Boolean> opens = new ArrayList<>();
        TextView uncommonView = (TextView) findViewById(R.id.uncommonText);
        TextView bookstoreView = (TextView) findViewById(R.id.bookstoreText);
        TextView canadayView = (TextView) findViewById(R.id.canadayText);
        TextView carpenterView = (TextView) findViewById(R.id.carpenterText);
        TextView collierView = (TextView) findViewById(R.id.collierText);
        TextView erdmanView = (TextView) findViewById(R.id.erdmanText);
        TextView newdormView = (TextView) findViewById(R.id.newdormText);
        TextView gymView = (TextView) findViewById(R.id.gymText);
        TextView nextHavView = (TextView) findViewById(R.id.nextHavBus);
        TextView nextBMCView = (TextView) findViewById(R.id.nextBMCBus);

        isOpen(uncommonView, uncommon);
        isOpen(bookstoreView, bookstore);
        isOpen(canadayView, canaday);
        isOpen(carpenterView, carpenter);
        isOpen(collierView, collier);
        isOpen(erdmanView, erdman);
        isOpen(newdormView, newdorm);
        isOpen(gymView, gym);
        DisplayNextBlueBus(nextBMCView,BMCtoHAV);
        DisplayNextBlueBus(nextHavView,HAVtoBMC);
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
                        String ampm = thisTime.substring(thisTime.length()-2);
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

    private ArrayList<ArrayList<String>> newScheduleBlueBus (String filename){
        AssetManager assetManager = getAssets();
        ArrayList<ArrayList<String>> thisSchedule = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            ArrayList<String> newArray = new ArrayList<>();
            thisSchedule.add(newArray);
        }
        try {
            InputStream inputStream = assetManager.open(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            int counter = 0;
            while((line = in.readLine()) != null) {
                String[] tokens = line.split("\\s", 0);
                for (int i = 1; i<tokens.length; i++){
                    String thisTime = tokens[i];
                    System.out.println("thisTime: " + thisTime);
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
                        //deal with cross midnight times
                        if(finalTime.substring(0,2).compareTo("00")== 0 || finalTime.substring(0,2).compareTo("07") < 0) {
                            thisSchedule.get((counter+1)%7).add(finalTime); //the time actually belongs to next day
                        } else {
                            thisSchedule.get(counter).add(finalTime);
                        }
                    }
                }
                counter++;
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        //sort the Monday times since cross midnights times on Sunday is appended at the end of Monday list
        // which is the earliest bus on Monday
        Collections.sort(thisSchedule.get(0));


        //System.out.println("schedule formatted: ");
        //for(int i = 0; i < thisSchedule.size(); i++){
        //    System.out.println(thisSchedule.get(i));
        //}
        return thisSchedule;
    }

    public void DisplayNextBlueBus(TextView view, ArrayList<ArrayList<String>> schedule){
        String[] timeDetails = timeNow().split("\\s", 0);
        int day = Integer.parseInt(timeDetails[0]);
        String timeOfDay = timeDetails[1];
        ArrayList<String> thisDayTimes = schedule.get(day);
        ArrayList<String> nextDayTimes = schedule.get((day+1)%7);
        // return next blue bus
        // next blue bus is on today
        int counter = 0;
        String result = ""; //store next 3 buses times
        for(int i = 0; i < thisDayTimes.size(); i++){
            if(timeOfDay.compareTo(thisDayTimes.get(i)) <= 0){
                result = result + toRegularTime(thisDayTimes.get(i))+ "\n";
                counter++;
                if(counter == 3){
                    result = result.substring(0, result.length()-1);
                    break;
                }
            }
        }
        //next bus is on next day
        if(counter < 3){
            for(int i = 0; i < nextDayTimes.size(); i++){
                if(timeOfDay.compareTo(nextDayTimes.get(i)) <= 0){
                    result = result + toRegularTime(nextDayTimes.get(i))+ "\n";
                    counter++;
                    if(counter == 3){
                        result = result.substring(0, result.length()-1);
                        break;
                    }
                }
            }
        }
        view.setText(result);

    }

    // isOpen(R.id.uncommon, uncommon);
    public void isOpen(TextView view, ArrayList<ArrayList<String>> schedule){
        System.out.println("schedule formatted: ");
        for(int i = 0; i < schedule.size(); i++){
            System.out.println(schedule.get(i));
        }
        String[] timeDetails = timeNow().split("\\s", 0);
        int day = Integer.parseInt(timeDetails[0]);
        String timeOfDay = timeDetails[1];
        //System.out.println("timeOfDay: "+ timeOfDay);
        ArrayList<String> thisDayTimes = schedule.get(day);
        // for each pair of open-closed times, find if open or not
        for(int i = 0; i < thisDayTimes.size(); i = i+2){
            if(timeOfDay.compareTo(thisDayTimes.get(i)) >= 0 &&
                    (timeOfDay.compareTo(thisDayTimes.get(i+1)) < 0
                            || thisDayTimes.get(i+1).compareTo(midnight) == 0)){
                view.setText("OPEN until " + toRegularTime(thisDayTimes.get(i+1)));
                return;
            }
        }
        //if not open, on the current day, find the next open time
        if(timeOfDay.compareTo(thisDayTimes.get(0)) < 0){
            view.setText("CLOSED until TODAY at: " + toRegularTime(thisDayTimes.get(0)));
            return;
        }
        for(int i=1; i<thisDayTimes.size(); i = i+2){
            if(i+1 < thisDayTimes.size()) { //take care of the case when the next openning is on next day
                if (timeOfDay.compareTo(thisDayTimes.get(i)) >= 0 &&
                        (timeOfDay.compareTo(thisDayTimes.get(i + 1)) < 0
                                || thisDayTimes.get(i + 1).compareTo(midnight) == 0)) {
                    view.setText("CLOSED until TODAY at: " + toRegularTime(thisDayTimes.get(i + 1)));
                    return;
                }
            }
        }
        // if not open, and the next opening is on the next day
        ArrayList<String> nextDayTimes = schedule.get(day+1);
        view.setText("CLOSED until TOMORROW at: " + toRegularTime(nextDayTimes.get(0)));
        return;
    }

    // parses a string in nextDayTimes into regular time
    private String toRegularTime (String twentyfourTime){
        String hourString = "";
        // "17:00"
        // if the string starts with a 0, remove it
        if(twentyfourTime.charAt(0) == '0'){
            twentyfourTime = twentyfourTime.substring(1);
        }
        // take first characters until ":", convert into integer
        String subs = twentyfourTime.substring(0,twentyfourTime.indexOf(":"));
        int hour = Integer.parseInt(subs);
        // if that int > 12, convert
        if(hour > twelve){
            hour = hour-twelve; // subtract 12 from int
            // add "pm" to end of
            hourString = "" + hour + twentyfourTime.substring(twentyfourTime.indexOf(":")) + "pm";
        } else if (hour == twelve){ // for noon
            // add "pm" to end of
            hourString = "" + hour + twentyfourTime.substring(twentyfourTime.indexOf(":")) + "pm";
        } else {
            // else add "am" to end of
            hourString = "" + hour + twentyfourTime.substring(twentyfourTime.indexOf(":")) + "am";
        }
        if(hourString.charAt(0) == '0'){ // if midnight
            hourString = "12" + hourString.substring(1) + "am";
        }
        System.out.println("hourString: " + hourString);
        return hourString;
    }

    // view.getText().toString()+

    private String timeNow(){ // work for later: we only want the day of the week and time

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

    public void blueBusRedirect(View view){
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("https://www.brynmawr.edu/transportation/blue-bus-bi-co"));
        startActivity(viewIntent);
    }

    public void diningHallRedirect(View view){
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("https://www.brynmawr.edu/dining/menus-and-hours"));
        startActivity(viewIntent);
    }

    public void libraryRedirect(View view){
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("https://www.brynmawr.edu/lits/hours"));
        startActivity(viewIntent);
    }

    public void bookstoreRedirect(View view){
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("https://bookshop.brynmawr.edu"));
        startActivity(viewIntent);
    }

    public void gymRedirect(View view){
        Intent viewIntent =
                new Intent("android.intent.action.VIEW",
                        Uri.parse("http://athletics.brynmawr.edu/information/facilities/fitnesscenterpoolhours"));
        startActivity(viewIntent);
    }

}
