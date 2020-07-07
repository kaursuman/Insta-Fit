package com.example.insta_fit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.insta_fit.Model.Register;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class UserProfile extends AppCompatActivity implements View.OnClickListener, TextWatcher {
    private EditText etlname,etfname,etage,etheight,etweight,etbmi;
    private Button btnUpdate;
    private String unm,pwd,lstName,fstName,gender="";
    private String dbId ="";
    private Integer userAge;
    private Double userHeight,userWeight,userBmi;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    DatabaseReference mDatabase;
    TextView tverror;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "myPrefs";
    private static final String KEY_FIRSTNAME = "first";
    private static final String KEY_LASTNAME = "last";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("register");
        firebaseUser = mFirebaseAuth.getCurrentUser();
        dbId = firebaseUser.getEmail();
        etlname = (EditText) findViewById(R.id.editTextLName);
        etfname = (EditText) findViewById(R.id.editTextFName);
        etage = (EditText) findViewById(R.id.editTextAge);
        etheight = (EditText) findViewById(R.id.editTextHeight);
        etweight = (EditText) findViewById(R.id.editTextWeight);
        etbmi = (EditText) findViewById(R.id.editTextBMI);
        btnUpdate = (Button) findViewById(R.id.btnUpd);
        tverror = findViewById(R.id.tvError);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
        {
            unm = bundle.getString("userid");
        }
        //tverror.setText(dbId);



        etfname.setText(sharedPreferences.getString(KEY_FIRSTNAME,""));
        etlname.setText(sharedPreferences.getString(KEY_LASTNAME,""));

        etfname.addTextChangedListener(this);
        etlname.addTextChangedListener(this);
        mDatabase.addValueEventListener(new ValueEventListener()
        {
            boolean result=true;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                while (items.hasNext())
                {
                    DataSnapshot item = items.next();
                    final String dbEmail = item.child("email").getValue().toString();
                    result=false;
                    result = dbId.equalsIgnoreCase(dbEmail);
                    if (result == true)
                    {
                        lstName = item.child("lastName").getValue().toString();
                        etlname.setText(lstName);
                        fstName = item.child("firstName").getValue().toString();
                        etfname.setText(fstName);
                        gender = item.child("gender").getValue().toString();
                        //tverror.setText(gender);
                        pwd = item.child("password").getValue().toString();
                        //tverror.setText(pwd);
                        Integer userAge = Integer.parseInt(item.child("age").getValue().toString());
                        etage.setText(Integer.toString(userAge));
                        Double userHeight = Double.parseDouble(item.child("height").getValue().toString());
                        etheight.setText(Double.toString(userHeight));
                        Double userWeight = Double.parseDouble(item.child("weight").getValue().toString());
                        etweight.setText(Double.toString(userWeight));
                        Double userBmi = (userWeight * 10000) / (userHeight * userHeight);
                        etbmi.setText(Double.toString(userBmi));
                        Register reg = new Register(lstName, fstName, dbId, pwd, gender, userAge, userHeight, userWeight, userBmi);
                        //mDatabase.child(pwd).setValue(reg);
                    }
                    else if(result != true)
                    {
                        continue;
                    }
                    else
                    {
                        Toast.makeText(UserProfile.this, "Unable to connect with DB", Toast.LENGTH_LONG).show();
                    }
                }
                mDatabase.child("register").removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });

        //btnShow.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v)
    {
                final String ln = etlname.getText().toString();
                final String fn = etfname.getText().toString();
                final String em = dbId;
                final String p = pwd;
                final String g = gender;
                //tverror.setText(g);
                final Integer usage = Integer.parseInt(etage.getText().toString());
                final Double uht = Double.parseDouble(etheight.getText().toString());
                final Double uwt = Double.parseDouble(etweight.getText().toString());
                Double usrbmi = (uwt * 10000) / (uht * uht);
                etbmi.setText(Double.toString(usrbmi));
                //tverror.setText(Double.toString(uwt));
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("register");
                Register regF = new Register(ln, fn, em, p, g, usage,uht,uwt, usrbmi);
                reference.child(p).setValue(regF);
                Snackbar.make(v, "This user has been updated successfully", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void managePref() {
            editor.putString(KEY_FIRSTNAME,etfname.getText().toString().trim());
            editor.putString(KEY_LASTNAME,etlname.getText().toString().trim());
            editor.apply();
    }
}
