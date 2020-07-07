package com.example.insta_fit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class HowActive extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    List<String> list;
    TextView activeText;
    Spinner dropdown;
    Spinner goal;
    String tmp;
    int intGoal;
    SharedPreferences mPrefs;
    SharedPreferences.Editor editor;
    Intent intent;
    float actFac;

    GifImageView sedRun;
    GifImageView lightRun;
    GifImageView activeRun;

    public void toNextActivity(View view){
        intent = new Intent(this, CheckCaloryStats.class);
        if (dropdown.getSelectedItem().toString() == "Sedentary"){
            actFac = (float) 1.2;
        }
        else if (dropdown.getSelectedItem().toString() == "Lightly Active"){
            actFac = (float) 1.3;
        }
        else{
            actFac = (float) 1.4;
        }

        if (goal.getSelectedItem() != null){
            if (goal.getSelectedItem().toString() == "Maintain Weight"){
                intGoal = 1;
            }
            else if (goal.getSelectedItem().toString() == "Gain Weight"){
                intGoal = 3;
            }
            else{
                intGoal = 2;
            }
        }

        editor = mPrefs.edit();
        editor.putFloat("ACTIVITY_FACTOR",actFac);
        editor.putInt("GOAL", intGoal);
        editor.apply();
        startActivity(intent);
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        if (dropdown.getSelectedItem().toString() == "Sedentary"){

            tmp = "The average person is sedentary. Anyone that spends less than 30 minutes per day intentionally exercising, is considered sedentary. \n"+
            "Some of your activities may include working at your desk, walking your dog, shopping at Publix.";
            activeText.setText(tmp);

            sedRun.setVisibility(View.VISIBLE);
            lightRun.setVisibility(View.INVISIBLE);
            activeRun.setVisibility(View.INVISIBLE);
        }

        if (dropdown.getSelectedItem().toString() == "Lightly Active"){

            tmp ="A user that spends at least 30 minutes or more intentionally exercising.\n" +
                    "Your activities may include walking the dog, mowing the lawn, gardening, being a car sales man, or even just jogging around the block.";

            activeText.setText(tmp);

            sedRun.setVisibility(View.INVISIBLE);
            lightRun.setVisibility(View.VISIBLE);
            activeRun.setVisibility(View.INVISIBLE);

        }

        if (dropdown.getSelectedItem().toString() == "Active"){

            tmp ="A user that spends 1.5-2 hours out of their day intentionally exercising. \n" +
                    "Their normal daily activities may include waiting tables, mowing the lawn, or walking their dog. However most Active users set aside an extra 1-2 hours a day just for exercise.";
            activeText.setText(tmp);

            sedRun.setVisibility(View.INVISIBLE);
            lightRun.setVisibility(View.INVISIBLE);
            activeRun.setVisibility(View.VISIBLE);
        }

        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_active);

      activeText = findViewById(R.id.activeText);


        //get the spinner from the xml.
        dropdown = findViewById(R.id.activeSpinner);

        String[] choices = new String[]{"Sedentary", "Lightly Active", "Active"};
        list = new ArrayList<>(Arrays.asList(choices));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);

        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        goal = findViewById(R.id.spinner2);
        //create a list of items for the spinner.
        String[] items2 = new String[]{"Maintain Weight", "Lose Weight", "Gain Weight"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        //set the spinners adapter to the previously created one.
        goal.setAdapter(adapter2);
        goal.setOnItemSelectedListener(this);

        //images
        sedRun = (GifImageView) findViewById(R.id.sedRun);
        lightRun = (GifImageView) findViewById(R.id.lightRun);
        activeRun = (GifImageView) findViewById(R.id.activeRun);

        mPrefs = getSharedPreferences("com.example.insta_fit", MODE_PRIVATE);
        String fName = mPrefs.getString("FIRST_NAME", "noData");
        String lName = mPrefs.getString("LAST_NAME", "noData");
        Integer weight = mPrefs.getInt("WEIGHT", 0);
        Integer height = mPrefs.getInt("HEIGHT", 0);
        Integer age = mPrefs.getInt("AGE", 0);
        String gender = mPrefs.getString("GENDER", "noData");

    }
    public void backButton(View view){
        Intent intent = new Intent(getApplicationContext(), FinalHomeActivity.class);
        startActivity(intent);
    }
}
