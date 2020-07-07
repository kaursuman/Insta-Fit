package com.example.insta_fit.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DB {

    private static final String TBSTEPS="stepstb";

    private static final String UEMAIL="uemail";

    private static final String DATE="sdate";
    private static final String STEPS="steps";

    SQLiteDatabase sqldb;
    Context con;


    public void update(String date, String uid, String steps)
    {
        Cursor c=sqldb.query(TBSTEPS,new String[]{STEPS},DATE + " = '"+date+"' AND "+ UEMAIL +" = '"+uid+"'",null,null,null,null);
        int i=0;
        if(c.getCount()>0)
        {
            c.moveToFirst();
            i= Integer.parseInt(c.getString(0))+ Integer.parseInt(steps);

            ContentValues cv=new ContentValues();
            cv.put(STEPS,""+i);
            sqldb.update(TBSTEPS,cv,DATE + " = '"+date+"' AND "+ UEMAIL +" = '"+uid+"'",null);
        }
        else
        {
            ContentValues cv=new ContentValues();
            cv.put(UEMAIL,uid);
            cv.put(DATE,date);
            cv.put(STEPS,steps);
            sqldb.insert(TBSTEPS,null,cv);
        }
    }

    public String getsteps(String uid)
    {
        String ans="";
        Cursor c=sqldb.query(TBSTEPS,new String[]{DATE,STEPS},UEMAIL +" = '"+uid+"'",null,null,null,DATE);
        if(c.getCount()>0)
        {
            while (c.moveToNext())
            {
                ans+=c.getString(0)+"*"+c.getString(1)+"#";
            }
        }
        else
        {
            ans="no";
        }

        return ans;
    }
}

