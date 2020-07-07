package com.example.insta_fit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {
    TextView text;
    private FirebaseAuth mFirebaseAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        text= (TextView) findViewById(R.id.welcomeUser);
        mFirebaseAuth = FirebaseAuth.getInstance();
        String str = intent.getStringExtra("message");
        if(str != null)
        {
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("register");
            //reference.getParent().child().;
        }
        text.setText(str);
    }

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login_Form.class));
    }
}
