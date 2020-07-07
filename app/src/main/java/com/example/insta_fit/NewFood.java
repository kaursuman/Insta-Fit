package com.example.insta_fit;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class NewFood extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    Gson gson = new Gson();
    TableRow quanttb;
    TextView txtCategory,txtFood,totcount,txtSelected,etCal;
    ImageView plus,minus;
    List<String> list;
    String spinitems[]=new String[]{"Meal","Fruit","Beverage","Snack","Dessert"};
    Spinner categorySpinner;
    Spinner foodSpinner;
    String cat,foodName,selName;
    Double calories;
    String sel;
    int count=2;
    Button submit;
    TextView caloriesPerServing;
    String dbId="";

    SharedPreferences mPref;
    SharedPreferences.Editor editor;
    RelativeLayout ray;

    String[] countitem = new String[]{"0.25","0.50","1","2","3","4","5","6","7","8","9","10"};
    String[] foodList = new String[]{""};

    DatabaseReference databaseReference;
    FirebaseUser firebaseUser1;
    FirebaseAuth mFirebaseAuth;
    ArrayList<String> spinnerFoodData;
    ArrayList<Double> caloriesData;
    String[] choices;

    ArrayList<String> foodCategories;
    ArrayList<String> arrayList_Meal,arrayList_Dessert,arrayList_Snack,arrayList_Fruit,arrayList_Beverage;
    ArrayAdapter<String> adapter1,adapter2;
    ListIterator<String> iterator;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_food);
        mPref = getSharedPreferences("com.example.insta_fit", MODE_PRIVATE);
        editor = mPref.edit();
        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseUser1 = mFirebaseAuth.getCurrentUser();
        dbId = firebaseUser1.getEmail();

        quanttb= (TableRow) findViewById(R.id.quanttb);
        plus= (ImageView) findViewById(R.id.plus);
        minus= (ImageView) findViewById(R.id.minus);
        totcount= (TextView) findViewById(R.id.count);
        txtCategory = (TextView)findViewById(R.id.textViewCategory);
        txtFood = (TextView)findViewById(R.id.textViewFood);
        txtSelected=(TextView)findViewById(R.id.textViewSelected);
        etCal=(TextView) findViewById(R.id.textCalories);

        databaseReference = FirebaseDatabase.getInstance().getReference("fooditem");

        categorySpinner = (Spinner)findViewById(R.id.spinnerCategory);
        foodSpinner = (Spinner)findViewById(R.id.spinnerFood);
        foodCategories = new ArrayList<>();
        foodCategories.add("Meal");
        foodCategories.add("Fruit");
        foodCategories.add("Beverage");
        foodCategories.add("Snack");
        foodCategories.add("Dessert");

        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, foodCategories);

        categorySpinner.setAdapter(adapter1);
        categorySpinner.setOnItemSelectedListener(this);


        ray=(RelativeLayout)findViewById(R.id.adray);

        spinnerFoodData = new ArrayList<>();
        adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerFoodData);
        foodSpinner.setAdapter(adapter2);
        foodSpinner.setOnItemSelectedListener(this);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count>=0 && count<=10)
                {
                    count++;
                    totcount.setText(countitem[count]);
                }
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(count>=1 && count<=11)
                {
                    count--;
                    totcount.setText(countitem[count]);
                }
            }
        });

    }

    public void submitFood(View view){
        FoodItem newItem = new FoodItem(cat, foodName,calories, count);
        String json = gson.toJson(newItem);
        editor.putString("NEW_FOOD", json);
        editor.apply();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        spinnerFoodData.clear();
        if (i==0)
        {
            arrayList_Meal = new ArrayList<>();
            sel="Meal";
        }
        if (i==1)
        {
            arrayList_Fruit = new ArrayList<>();
            sel = "Fruit";
        }
        if (i==2)
        {
            arrayList_Beverage = new ArrayList<>();
            sel = "Beverage";
        }
        if (i==3)
        {
            arrayList_Snack = new ArrayList<>();
            sel = "Snack";
        }
        if (i==4)
        {
            arrayList_Dessert = new ArrayList<>();
            sel = "Dessert";
        }
        if (categorySpinner.getSelectedItem().toString() == "Meal"){

            sel= categorySpinner.getSelectedItem().toString();
        }

        if (categorySpinner.getSelectedItem().toString() == "Fruit"){
            sel= categorySpinner.getSelectedItem().toString();
        }

        if (categorySpinner.getSelectedItem().toString() == "Beverage"){
            sel= categorySpinner.getSelectedItem().toString();
        }
        if (categorySpinner.getSelectedItem().toString() == "Snack"){
            sel= categorySpinner.getSelectedItem().toString();
        }
        if (categorySpinner.getSelectedItem().toString() == "Dessert"){

            sel= categorySpinner.getSelectedItem().toString();
        }

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                    Iterator<DataSnapshot> myItems = dataSnapshot.getChildren().iterator();
                    while (myItems.hasNext())
                    {
                        DataSnapshot myItem = myItems.next();
                        final String s1 = myItem.child("category").getValue().toString();
                        if(sel.equalsIgnoreCase(s1))
                        {
                            //Toast.makeText(NewFood.this, "DB is Connected", Toast.LENGTH_LONG).show();
                            cat = myItem.child("category").getValue().toString();
                            foodName = myItem.child("name").getValue().toString();
                            spinnerFoodData.add(foodName);
                            calories = Double.parseDouble(myItem.child("calories").getValue().toString());
                            etCal.setText(Double.toString(calories));
                            //txtSelected.setText(Double.toString(calories));
                        }
                        else if(sel.equalsIgnoreCase(s1) == false)
                        {
                            continue;
                        }
                        else
                        {
                            Toast.makeText(NewFood.this, "Unable to connect with DB", Toast.LENGTH_LONG).show();
                        }
                    }
                    adapter2.notifyDataSetChanged();

                    databaseReference.child("fooditem").removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //txtSelected.setText(spinnerFoodData.indexOf(0));

        iterator = spinnerFoodData.listIterator();
        while (iterator.hasNext())
        {
            txtSelected.setText("abc");
            if(iterator.next().equalsIgnoreCase(foodSpinner.getSelectedItem().toString()))
            {
                txtSelected.setText("def");
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> myItems = dataSnapshot.getChildren().iterator();
                        while (myItems.hasNext()) {
                            DataSnapshot myItem = myItems.next();
                            final String s2 = myItem.child("name").getValue().toString();
                            if(foodSpinner.getSelectedItem().toString().equalsIgnoreCase(s2))
                            {
                                Double calories = Double.parseDouble(myItem.child("calories").getValue().toString());
                                etCal.setText(Double.toString(calories));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
