package com.example.insta_fit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.insta_fit.Model.FoodItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Iterator;

public class CheckCaloryStats extends AppCompatActivity{
    String gender;
    double height;
    double weight;
    double suggestedIntake;
    double activityFactor;
    private String mydbId ="";
    float bmi;
    int age;
    double inchToCMRatio = 2.54;
    double lbsToKGRatio = 0.453592;
    private FirebaseAuth mFirebaseAuth;
    private String fname,lname;
    private double finalbmi;

    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    String tmpStr="";
    TextView greetingText;
    TextView calorieText;
    TextView todayCalorieText;
    TextView progressMessage;
    TextView bmiText;
    TextView burnedCalories;
    int caloriesToday;
    int calBurn;
    Button run,food;
    //Get the current day below
    int day = ((int) System.currentTimeMillis() / 86400000);
    static final int NEW_FOOD_REQUEST = 1;
    static final int EXERCISE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_calory_stats);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("register");
        firebaseUser = mFirebaseAuth.getCurrentUser();
        mydbId = firebaseUser.getEmail();

        mPref = getSharedPreferences("com.example.insta_fit", MODE_PRIVATE);
        editor = mPref.edit();
        greetingText = findViewById(R.id.greetingText);
        todayCalorieText = findViewById(R.id.currentCalories);
        progressMessage = findViewById(R.id.progressMessage);
        bmiText = findViewById(R.id.bmiText);
        run=findViewById(R.id.runButton);
        food=findViewById(R.id.addfood);
        burnedCalories = findViewById(R.id.burnedCalories);


        mDatabase.addValueEventListener(new ValueEventListener() {
            boolean result=true;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext())
                {
                    DataSnapshot item = items.next();
                    final String dbEmail = item.child("email").getValue().toString();
                    result=false;
                    result = mydbId.equalsIgnoreCase(dbEmail);
                    if (result == true)
                    {
                        lname = item.child("lastName").getValue().toString();
                        fname = item.child("firstName").getValue().toString();
                        gender = item.child("gender").getValue().toString();
                        age = Integer.parseInt(item.child("age").getValue().toString());
                        greetingText.setText("Hello"+ " "+ fname + " " +lname + "!!");
                        height = Double.parseDouble(item.child("height").getValue().toString());
                        weight = Double.parseDouble(item.child("weight").getValue().toString());
                        finalbmi = (weight * 10000) / (height * height);
                        if (finalbmi < 18.5){
                            tmpStr = "Your BMI: " + Double.toString(finalbmi) + "\n\n\nYou're considered underweight.";
                        }
                        else if (finalbmi < 25){
                            tmpStr = "Your BMI: " + Double.toString(finalbmi) + "\n\n\nYou're considered normal weight.";
                        }
                        else if (finalbmi < 30){
                            tmpStr = "Your BMI: " + Double.toString(finalbmi) + "\n\n\nYou're considered overweight.";
                        }
                        else {
                            tmpStr = "Your BMI: " + Double.toString(finalbmi) + "\n\n\nYou're considered obese.";
                        }
                        bmiText.setText(tmpStr);

                        if (day != mPref.getInt("DAY_OF_PREVIOUS_MEAL", 0)){
                            caloriesToday = 0;
                            calBurn = 0;
                        }
                        else{
                            caloriesToday = mPref.getInt("DAILY_CALORIES", 0);
                            calBurn = mPref.getInt("CALORIES_BURNED_TODAY", 0);
                        }

                        tmpStr = "Calories consumed today:\n" + caloriesToday;
                        todayCalorieText.setText(tmpStr);
                        tmpStr = "Calories actively burned today:\n" + calBurn;
                        burnedCalories.setText(tmpStr);


                        activityFactor = mPref.getFloat("ACTIVITY_FACTOR", (float) 1.3);
                        if (gender == "male"){
                            suggestedIntake = (double) ((10 * weight + 6.25 * height - 5 * age + 5) * activityFactor);
                        }
                        else if (gender == "female"){
                            suggestedIntake = (double) ((10 * weight + 6.25 * height - 5 * age - 161) * activityFactor);
                        }

                        suggestedIntake=1200.6789;

                        int goal = mPref.getInt("GOAL", 1);
                        calorieText = findViewById(R.id.recCalories);
                        if (goal == 1) {
                            tmpStr = "Your Recommended Daily Intake:\n" + Math.round(suggestedIntake) + " Calories";
                            calorieText.setText(tmpStr);
                        }
                        else if (goal == 2) {
                            tmpStr = "Your Recommended Daily Intake for Weight Loss:\n" + Math.round(suggestedIntake * 0.8) + " Calories";
                            calorieText.setText(tmpStr);
                        }
                        else if (goal == 3){
                            tmpStr = "Your Recommended Daily Intake for Weight Gain:\n" + Math.round(suggestedIntake * 1.2) + " Calories";
                            calorieText.setText(tmpStr);
                        }
                    }
                    else if(result != true)
                    {
                        continue;
                    }
                    else
                    {
                        Toast.makeText(CheckCaloryStats.this, "Unable to connect with DB", Toast.LENGTH_LONG).show();
                    }
                }
                mDatabase.child("register").removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
    }

    @Override
    public void onBackPressed(){
        startActivity(new Intent(getApplicationContext(),HowActive.class));
    }

    public void addFood(View view) {
        Intent newFood = new Intent(this, NewFood.class);
        startActivityForResult(newFood, NEW_FOOD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_FOOD_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Gson gson = new Gson();
                String json = mPref.getString("NEW_FOOD", "noData");
                FoodItem foodItem1 = gson.fromJson(json, FoodItem.class);
                caloriesToday += (foodItem1.getCalories() * foodItem1.getServings());
                tmpStr = "Calories consumed today:\n" + caloriesToday;
                todayCalorieText.setText(tmpStr);
                editor.putInt("DAY_OF_PREVIOUS_MEAL", day);
                editor.putInt("DAILY_CALORIES", caloriesToday);
                editor.apply();
                if (caloriesToday <= suggestedIntake - 100 && caloriesToday >= suggestedIntake - 500){
                    tmpStr = "You're almost there!";
                    progressMessage.setText(tmpStr);
                    progressMessage.setVisibility(View.VISIBLE);
                }
                else if (caloriesToday > suggestedIntake - 100 && caloriesToday < suggestedIntake + 100){
                    tmpStr = "You've hit your goal!";
                    progressMessage.setText(tmpStr);
                    progressMessage.setVisibility(View.VISIBLE);
                }
                else if (caloriesToday > suggestedIntake + 100){
                    tmpStr = "Whoa! You've eaten too much today!";
                    progressMessage.setText(tmpStr);
                    progressMessage.setVisibility(View.VISIBLE);
                }
                else {
                    progressMessage.setVisibility(View.INVISIBLE);
                }
            }
        }
        if (requestCode == EXERCISE_REQUEST){
            if (resultCode == RESULT_OK){
                calBurn += (int) Math.round((mPref.getInt("STEP_NUMBER", 0) * 0.05));
                tmpStr = "Calories actively burned today:\n" + calBurn;
                burnedCalories.setText(tmpStr);
                editor.putInt("CALORIES_BURNED_TODAY", calBurn);
                editor.apply();
            }
        }
    }

    public void button5(View view) {

        Intent intent2 = new Intent(getApplicationContext(), SetTargets.class);
        startActivityForResult(intent2, EXERCISE_REQUEST);

    }

    public void referenceButton(View view){
        Intent refIntent = new Intent(CheckCaloryStats.this, ReferenceLinks.class);
        startActivity(refIntent);
    }
}
