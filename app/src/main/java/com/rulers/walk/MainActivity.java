package com.rulers.walk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.rulers.walk.Service.MySensorService;
import com.rulers.walk.SharedPreference.Check;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class MainActivity extends AppCompatActivity {

    private final int PERMISSION_CODE=101;

    ImageView img;
    TextView stepText;
    TextView distancetxt;
    TextView calorietxt;
    TextView timetxt;

    private Intent serviceIntent;

    static float weight;
    static double height;
    static int steps;

    final static double walkingFactor = 0.57;

    static double CaloriesBurnedPerMile;

    static double strip;

    static double stepCountMile; // step/mile

    static double conversationFactor;

    static double CaloriesBurned;

    static NumberFormat formatter = new DecimalFormat("#0.0");

    static double distance;

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);


        stepText=findViewById(R.id.steptext);
        img=findViewById(R.id.image);
        distancetxt=findViewById(R.id.distance);
        timetxt=findViewById(R.id.time);
        calorietxt=findViewById(R.id.calorie);

        serviceIntent = new Intent(this, MySensorService.class);

        if(isMyServiceRunning(MySensorService.class))
        {
            Drawable drawable= AppCompatResources.getDrawable(this,R.drawable.ic_baseline_stop_24);
            img.setImageDrawable(drawable);
        }
        else
        {
            Drawable drawable= AppCompatResources.getDrawable(this,R.drawable.ic_baseline_play_arrow_24);
            img.setImageDrawable(drawable);
        }
//        sp=Check.createSharedPreference(this);
//        if(sp.getInt("flag",1)==0)
//        {
////            switchMaterial.setText("Pause");
//            Drawable drawable= AppCompatResources.getDrawable(this,R.drawable.ic_baseline_stop_24);
//            img.setImageDrawable(drawable);
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp=Check.createSharedPreference(this);
        steps=sp.getInt("fsteps",0);
        stepText.setText(String.valueOf(steps));


        long minute=sp.getLong("time",0)/1000/60;
//        Toast.makeText(this, ""+minute, Toast.LENGTH_SHORT).show();
        if(minute==0)
        {
            timetxt.setText("-");
        }
        else {
            timetxt.setText(minute + "min");
        }

        height=Double.valueOf(sp.getString("height","0"))*2.54;
        weight=sp.getFloat("weight",0);

        calculateCalorie();
        calorietxt.setText(formatter.format(CaloriesBurned));
        distancetxt.setText(formatter.format(distance)+"m");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101)
        {
            if(permissions[0].contains(Manifest.permission.ACTIVITY_RECOGNITION)){
                if(grantResults[0]!= PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PERMISSION_CODE);
                }
            }
        }
    }

    public void stepAction(View view) {
        if(!sp.getString("height","").equals("") || sp.getFloat("weight",0f)!=0f) {
//            if (sp.getInt("flag", 1) == 1) {
            if(isMyServiceRunning(MySensorService.class)==false)
            {
                resetAction(view);
                ContextCompat.startForegroundService(this, serviceIntent);
//                Check.writeInt("flag", 0);

                Drawable drawable = AppCompatResources.getDrawable(this,R.drawable.ic_baseline_stop_24);
                img.setImageDrawable(drawable);
                img.setImageDrawable(drawable);
                Check.writeLong("stime", System.currentTimeMillis());
            } else {
                stopService(serviceIntent);
//                Check.writeInt("flag", 1);
                Drawable drawable = AppCompatResources.getDrawable(this,R.drawable.ic_baseline_play_arrow_24);
                img.setImageDrawable(drawable);
            }
        }
        else
        {
            Intent i = new Intent(this, Details.class);
            startActivity(i);
        }
    }

    public void resetAction(View view) {
        Check.writeInt("fsteps",0);
        Check.writeInt("flag",1);
        Check.writeLong("time",0l);
        Check.writeFloat("calorie",0);

        distancetxt.setText("0.0m");
        stepText.setText("0");
        timetxt.setText("-");
        calorietxt.setText("0.0");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.getdata) {
                Intent i = new Intent(this, Details.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void calculateCalorie()
    {

        CaloriesBurnedPerMile = walkingFactor * (weight * 2.2);

        strip = height * 0.415;

        stepCountMile = 160934.4 / strip;

        conversationFactor = CaloriesBurnedPerMile / stepCountMile;

        CaloriesBurned = steps * conversationFactor;


        distance = (steps * strip) / 1000;

//        Toast.makeText(this, "distance="+distance+" cal="+CaloriesBurned, Toast.LENGTH_SHORT).show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}