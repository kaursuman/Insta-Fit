package com.example.insta_fit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.insta_fit.Model.Register;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Signup_Form extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "MainActivity";
    private TextInputLayout tlLastName;
    private EditText etLastName;
    private TextInputLayout tlFirstName;
    private EditText etFirstName;
    private TextInputLayout tlEmail;
    private EditText etEmail;
    private TextInputLayout tlPass;
    private EditText etPass;
    private TextInputLayout tlConfirmPass,tilDob,tilHeight,tilWeight;
    private EditText etConfirmPass;
    private RadioGroup rgpGender;
    private EditText tvDate,etHeight,etWeight;
    private Button btnReg;
    private ArrayList<Register> listOfRegistrations;
    private Register register;
    private RadioButton rbMale, rbFemale;
    private String passwordInput, conPasswordInput,emailInput;
    private TextView tvError;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String email,password, confirmPassword, lastName,firstName,uid;
    ProgressBar myBar;

    FirebaseDatabase rootNode;
    DatabaseReference reference;

    //REGULAR EXPRESSION VARIABLES
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^"+
                    "(?=.*[0-9])"+
                    "(?=.*[a-z])"+
                    "(?=.*[A-Z])"+
                    "(?=.*[@#$%^&+=!*])"+
                    "(?=\\S+$)"+
                    ".{6,}" +
                    "$");
    private static final Pattern NAME_PATTERN =
            Pattern.compile("[A-Z][a-zA-Z]*");

    private static final Pattern AGE_PATTERN =
            Pattern.compile("[2-9][0-9]");
    private static final Pattern HEIGHT_PATTERN =
            Pattern.compile("[1-9][0-9][0-9]");
    private static final Pattern WEIGHT_PATTERN =
            Pattern.compile("[1-9][0-9]");
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup__form);
        tlLastName = findViewById(R.id.tilLastName);
        tlFirstName = findViewById(R.id.tilFirstName);
        tlEmail = findViewById(R.id.tilEmail);
        tlPass = findViewById(R.id.tilPassword);
        tlConfirmPass = findViewById(R.id.tilConfirmPass);
        tilDob=findViewById(R.id.tilDob);
        tilHeight=findViewById(R.id.tilHeight);
        tilWeight=findViewById(R.id.tilWeight);
        tvError=findViewById(R.id.tvError);
        etLastName = findViewById(R.id.editTextLastName);
        etFirstName = findViewById(R.id.editTextFirstName);
        etEmail = findViewById(R.id.editTextEmail);
        etPass = findViewById(R.id.editTextPassword);
        etConfirmPass = findViewById(R.id.editTextConfirmPass);
        etHeight=findViewById(R.id.editTextHeight);
        etWeight = findViewById(R.id.editTextWeight);
        rgpGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        listOfRegistrations=new ArrayList<Register>();

        // Variables for calculating age
        tvDate = findViewById(R.id.editTextDob);
        btnReg = findViewById(R.id.btnRegister);
        myBar = findViewById(R.id.pBar);
        mFirebaseAuth = FirebaseAuth.getInstance();
        if(mFirebaseAuth.getCurrentUser() != null)
        {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            getIntent().putExtra(lastName,firstName);
            finish();
        }
        btnReg.setOnClickListener(this);
    }

    private boolean validateLastName(String lNameInput)
    {
        if(lNameInput.isEmpty()){
            tlLastName.setError("Field can't be empty");
            return false;
        }else if(!NAME_PATTERN.matcher(lNameInput).matches()){
            tlLastName.setError("Please enter a valid last name");
            return false;
        }else{
            tlLastName.setError(null);
            return true;
        }
    }

    private boolean validateFirstName(String fNameInput)
    {
        if(fNameInput.isEmpty()){
            tlFirstName.setError("Field can't be empty");
            return false;
        }else if(!NAME_PATTERN.matcher(fNameInput).matches()){
            tlFirstName.setError("Please enter a valid first name");
            return false;
        }else{
            tlFirstName.setError(null);
            return true;
        }
    }

    private boolean validateEmail(String emailInput)
    {
        if(emailInput.isEmpty()){
            tlEmail.setError("Field can't be empty");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            tlEmail.setError("Please enter a valid email address");
            return false;
        }else{
            tlEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String passwordInput)
    {
        if(passwordInput.isEmpty()){
            tlPass.setError("Field can't be empty");
            return false;
        }else if(!PASSWORD_PATTERN.matcher(passwordInput).matches()){
            tlPass.setError("Password too weak");
            return false;
        }else{
            tlPass.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(String conPasswordInput)
    {
        if(conPasswordInput.isEmpty()){
            tlConfirmPass.setError("Field can't be empty");
            return false;
        }else if(!PASSWORD_PATTERN.matcher(conPasswordInput).matches()){
            tlConfirmPass.setError("Password too weak");
            return false;
        }else{
            tlConfirmPass.setError(null);
            return true;
        }
    }

    private boolean validateAge(String ageInput)
    {
        if(ageInput.isEmpty()){
            tilDob.setError("Field can't be empty");
            return false;
        }else if(!AGE_PATTERN.matcher(ageInput).matches()){
            tilDob.setError("Please Enter valid age");
            return false;
        }else{
            return true;
        }
    }

    private boolean validateHeight(String heightInput)
    {
        if(heightInput.isEmpty()){
            tilHeight.setError("Field can't be empty");
            return false;
        }else if(!HEIGHT_PATTERN.matcher(heightInput).matches()){
            tilHeight.setError("Please Enter valid Height");
            return false;
        }else{
            return true;
        }
    }

    private boolean validateWeight(String weightInput)
    {
        if(weightInput.isEmpty()){
            tilWeight.setError("Field can't be empty");
            return false;
        }else if(!WEIGHT_PATTERN.matcher(weightInput).matches()){
            tilWeight.setError("Please Enter valid Weight");
            return false;
        }else{
            return true;
        }
    }


    @Override
    public void onClick(View v)
    {
        try
        {
            lastName = etLastName.getText().toString().trim();
            validateLastName(lastName);
            firstName = etFirstName.getText().toString().trim();
            validateFirstName(firstName);
            email = etEmail.getText().toString().toLowerCase().trim();
            validateEmail(email);
            password = etPass.getText().toString().trim();
            validatePassword(password);
            confirmPassword = etConfirmPass.getText().toString().trim();
            validateConfirmPassword(confirmPassword);
            int rbId = rgpGender.getCheckedRadioButtonId();
            String gender = null;
            switch (rbId)
            {
                case R.id.rbMale:
                    gender = "Male";
                    break;
                case R.id.rbFemale:
                    gender = "Female";
                    break;
            }
            String temp=tvDate.getText().toString().trim();
            String temp1 = etHeight.getText().toString().trim();
            String temp2 = etWeight.getText().toString().trim();
            int age =0;
            double height=0.0,weight=0.0,bmi=0.0;
            if (!"".equals(temp))
            {   age =Integer.parseInt(temp);
            }
            if (!"".equals(temp1))
            {   height =Double.parseDouble(temp1);
            }
            if (!"".equals(temp2))
            {   weight =Double.parseDouble(temp2);
            }
            validateAge(temp);
            validateHeight(temp1);
            validateWeight(temp2);
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("register");
            Register register = new Register(lastName,firstName,email,password, gender,age,height,weight,bmi);
            reference.child(password).setValue(register);
        }catch(Exception e){}

        myBar.setVisibility(View.VISIBLE);

        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Signup_Form.this, "Sign-up Unsuccessful, Please try again!!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Signup_Form.this,"User Created",Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("user2", email);
                                        bundle.putString("pass2", password);
                                        Intent i = new Intent(getApplicationContext(), FinalHomeActivity.class);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                        finish();
                                    }
                                }
        });
    }
}
