package com.rulers.walk.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.rulers.walk.MainActivity;
import com.rulers.walk.R;
import com.rulers.walk.SharedPreference.Check;

public class MySensorService extends Service implements SensorEventListener {

    public static final String CHANNEL_ID = "SensorServiceChannel";
    private static int flag=0;
    public MySensorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        reRegisterSensor();

        //starting Notification
        startNotification();

//        long nextUpdate = Math.min(getTomorrow(), System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR);
//        AlarmManager am =(AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        return START_STICKY;
    }

    void reRegisterSensor()
    {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor s=sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(s!=null)
        {
            Log.d("Datainformation","Sensor Working");
            sm.registerListener(this,s,SensorManager.SENSOR_DELAY_NORMAL);
        }
        else
        {
            Log.d("Datainformation","Sensor Not Found");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        SharedPreferences sp= Check.createSharedPreference(this);
        Log.d("Datainformation","catch "+Math.round(event.values[0]));

        long oldtime=sp.getLong("stime",0);
        long newtime=System.currentTimeMillis()-oldtime;
        Log.d("Datainformation","time "+newtime);

        Check.writeLong("time",newtime);

        if(flag==0)
        {
            Check.writeInt("steps",Math.round(event.values[0]));
            flag=1;
        }
        else
        {
            int steps=sp.getInt("steps",0);
            Log.d("Datainformation","steps outside "+steps);

            int sensorstep=Math.round(event.values[0]);
            int finalsteps=sensorstep-steps;

            Log.d("Datainformation",steps+" fsteps "+finalsteps);

            if(finalsteps>0)
            {
                Check.writeInt("fsteps",finalsteps);
            }
        }

//        if(flag==0)
//        {
//            count=0;
//            flag=1;
//        }
//        count++;
//        check.writeshared(this,"fsteps",String.valueOf(count));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //Creating Notification for Foreground Service
    void startNotification()
    {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Walk")
                .setContentText("App runnung in Background")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
    }

    //Creating notification Channel
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("Datainformation","Service Destroy");
        stopSelf();
//        stopForeground(true);
    }
}