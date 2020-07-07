package com.example.insta_fit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class FinalHomeActivity extends AppCompatActivity implements View.OnClickListener
{
    private CardView cardViewAbout,cardViewProfile,cardViewTarget,cardViewStats,cardViewCoins,cardViewVideos;
    private TextView msg;
    private String mydbId ="";
    private String uname,pwd;
    private FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabase;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    private ProgressBar progressBarSteps;
    private ObjectAnimator progressAnimator;

    private String fname,lname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_home);
        init();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("register");
        firebaseUser = mFirebaseAuth.getCurrentUser();
        mydbId = firebaseUser.getEmail();
        cardViewAbout = (CardView) findViewById(R.id.cvAbout);
        cardViewProfile = (CardView) findViewById(R.id.cvProfile);
        cardViewTarget = (CardView) findViewById(R.id.cvTargets);
        cardViewStats = (CardView) findViewById(R.id.cvStats);
        cardViewCoins = (CardView) findViewById(R.id.cvCoins);
        cardViewVideos = (CardView) findViewById(R.id.cvVideos);
        msg = (TextView) findViewById(R.id.tvMsg);
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if(mFirebaseAuth.getCurrentUser() != null)
        {
            rootNode = FirebaseDatabase.getInstance();
            reference = rootNode.getReference("register");
            if (bundle != null)
            {
                uname= bundle.getString("user");
                pwd = bundle.getString("pass");
            }
        }

        cardViewAbout.setOnClickListener(this);
        cardViewProfile.setOnClickListener(this);
        cardViewTarget.setOnClickListener(this);
        cardViewStats.setOnClickListener(this);
        cardViewCoins.setOnClickListener(this);
        cardViewVideos.setOnClickListener(this);

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
                        msg.setText(fname +" " + lname);
                    }
                    else if(result != true)
                    {
                        continue;
                    }
                    else
                    {
                        Toast.makeText(FinalHomeActivity.this, "Unable to connect with DB", Toast.LENGTH_LONG).show();
                    }
                }
                mDatabase.child("register").removeEventListener(this);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {
            }
        });
        progressAnimator.setDuration(20000);
        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                progressBarSteps.setVisibility(View.GONE);
            }
        });
    }

    private void init()
    {
        progressBarSteps = findViewById(R.id.progressBarSteps);
        progressAnimator = ObjectAnimator.ofInt(progressBarSteps,"progress",0,2);

    }

    public void logout(View view)
    {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login_Form.class));
    }

    @Override
    public void onClick(View v) {
        Intent i;
        int btnId = v.getId();
        switch (btnId) {
            case R.id.cvAbout:
                i = new Intent(this, AboutApp.class);
                startActivity(i);
                break;
            case R.id.cvProfile:
                String id = firebaseUser.getEmail();
                Bundle bundle = new Bundle();
                bundle.putString("userid",uname);
                bundle.putString("password",pwd);
                i = new Intent(this, UserProfile.class);
                i.putExtras(bundle);
                startActivity(i);
                break;
            case R.id.cvTargets:
                i = new Intent(this, SetTargets.class);
                startActivity(i);
                break;
            case R.id.cvStats:
                i = new Intent(this, HowActive.class);
                startActivity(i);
                break;
            case R.id.cvCoins:
                i = new Intent(this, ReferenceLinks.class);
                startActivity(i);
                break;
            case R.id.cvVideos:
                i = new Intent(this, PlayVideos.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}

