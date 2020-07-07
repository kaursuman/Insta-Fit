package com.example.insta_fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.insta_fit.Model.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Login_Form extends AppCompatActivity implements View.OnClickListener,TextWatcher,
        CompoundButton.OnCheckedChangeListener
{
    /*private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^"+
                    "(?=.*[0-9])"+
                    "(?=.*[a-z])"+
                    "(?=.*[A-Z])"+
                    "(?=.*[@#$%^&+=])"+
                    "(?=\\S+$)"+
                    ".{6,}" +
                    "$"); */

    private TextInputLayout tilUserName;
    private EditText etUserName;
    private TextInputLayout tilPassword;
    private EditText etPassword;
    private Button bLogin, bSignUp;
    private Login login;
    private ArrayList<Login> listOfLogins;
    String userName,password;
    private CheckBox cbRemember;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASS = "password";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__form);

        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mFirebaseAuth = FirebaseAuth.getInstance();
        tilUserName = findViewById(R.id.textInputUserName);
        tilPassword = findViewById(R.id.textInputPassword);
        etUserName = findViewById(R.id.editTextUserName);
        etPassword = findViewById(R.id.editTextPassword);
        cbRemember = findViewById(R.id.checkboxRemember);
        bLogin = findViewById(R.id.btnLogin);
        bSignUp = findViewById(R.id.btnSignUp);
        listOfLogins=new ArrayList<Login>();
        mFirebaseAuth = FirebaseAuth.getInstance();

        if(sharedPreferences.getBoolean(KEY_REMEMBER,false))
            cbRemember.setChecked(true);
        else
            cbRemember.setChecked(false);

        etUserName.setText(sharedPreferences.getString(KEY_USERNAME,""));
        etPassword.setText(sharedPreferences.getString(KEY_PASS,""));

        etUserName.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        cbRemember.setOnCheckedChangeListener(this);

        if(mFirebaseAuth.getCurrentUser() != null)
        {
            Bundle bundle = new Bundle();
            bundle.putString("user", userName);
            bundle.putString("pass", password);
            Intent i = new Intent(this, FinalHomeActivity.class);
            i.putExtras(bundle);
            startActivity(i);
            finish();
        }
        bLogin.setOnClickListener(this);
        bSignUp.setOnClickListener(this);
    }

    private boolean validateEmail(){
        String emailInput = tilUserName.getEditText().getText().toString().trim();
        if(emailInput.isEmpty()){
            tilUserName.setError("Field can't be empty");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            tilUserName.setError("Please enter a valid email address");
            return false;
        }else{
            tilUserName.setError(null);
            return true;
        }
    }

    private boolean validatePassword(){
        String passwordInput = tilPassword.getEditText().getText().toString().trim();
        if(passwordInput.isEmpty()){
            tilPassword.setError("Field can't be empty");
            return false;
        }else{
            tilPassword.setError(null);
            return true;
        }
    }

    @Override
    public void onClick(View v)
    {
        int btnId = v.getId();
        try{
            userName = etUserName.getText().toString().trim();
            password = etPassword.getText().toString().trim();
            Login login = new Login(userName,password);
            switch (btnId)
            {
                case R.id.btnLogin:
                    if(validateEmail()==true && validatePassword() == true)
                    {
                        if(!(userName.isEmpty() && password.isEmpty()))
                        {
                            mFirebaseAuth.signInWithEmailAndPassword(userName,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(Login_Form.this, "Log-in error, Please try again!!"+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(Login_Form.this,"Login Success",Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("user", userName);
                                        bundle.putString("pass", password);
                                        Intent i = new Intent(Login_Form.this, FinalHomeActivity.class);
                                        i.putExtras(bundle);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(Login_Form.this, "Error occured!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case R.id.btnSignUp:
                    startActivity(new Intent(Login_Form.this, Signup_Form.class));
                    break;
            }
        }catch(NullPointerException e){}
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        managePref();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        managePref();
    }

    private void managePref() {
        if(cbRemember.isChecked()){
            editor.putString(KEY_USERNAME,etUserName.getText().toString().trim());
            editor.putString(KEY_PASS,etPassword.getText().toString().trim());
            editor.putBoolean(KEY_REMEMBER,true);
            editor.apply();
        }
        else
        {
            editor.putBoolean(KEY_REMEMBER,false);
            editor.remove(KEY_PASS);
            editor.remove(KEY_USERNAME);
            editor.apply();
        }
    }
}
