package com.example.pds;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.RoomDatabase;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    TextView txt_currentAccel, txt_prevAccel, txt_acceleration, txt_stepCount;
    ProgressBar prog_shakeMeter;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    private int counter = 0;

    private double accelerationCurrentValue;
    private double accelerationPreviousValue;
    private int changeInAcceleration;

    private int pointsPlotted = 5;
    private int graphIntervalCounter = 0;

    List<MainData> dataList = new ArrayList<>();
    RoomDB database;

    private Viewport viewport;

    private Vector<Integer> vec = new Vector<>();

    LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
            new DataPoint(0, 1),
            new DataPoint(1, 5),
            new DataPoint(2, 3),
            new DataPoint(3, 2),
            new DataPoint(4, 6)
    });

    private SensorEventListener sensorEventListener =  new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            //float x = sensorEvent.values[0];
            //float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            //accelerationCurrentValue = (int)Math.sqrt(x*x+ y*y+ z*z);
            accelerationCurrentValue = (int)Math.sqrt(z*z);
            changeInAcceleration = (int)Math.abs(accelerationCurrentValue- accelerationPreviousValue);
            vec.add(changeInAcceleration);
            accelerationPreviousValue = (int)accelerationCurrentValue;

            //step count


            //update text views
            txt_currentAccel.setText("Current: " + accelerationCurrentValue);
            txt_prevAccel.setText("Prev: " + accelerationPreviousValue);
            txt_acceleration.setText("Acceleration Change: " + changeInAcceleration);

            prog_shakeMeter.setProgress((int)changeInAcceleration);

            //change in color
            if(changeInAcceleration>10){
                txt_acceleration.setBackgroundColor(Color.RED);
                //Toast.makeText(MainActivity.this, " BIG Pothole Detected at " + step, Toast.LENGTH_LONG).show();
            }
            else if(changeInAcceleration>5){
                txt_acceleration.setBackgroundColor(Color.parseColor("#fcad03"));
                //Toast.makeText(MainActivity.this, "Pothole Detected at " + step, Toast.LENGTH_LONG).show();
            }
            else if(changeInAcceleration>0){
                txt_acceleration.setBackgroundColor(Color.parseColor("#42b3f5"));
            }
            else{
                txt_acceleration.setBackgroundColor(Color.GRAY);
            }

            //update the graph
            pointsPlotted++;
            if(pointsPlotted>1000){
                pointsPlotted = 1;
                counter = 0;
                series.resetData(new DataPoint[]{new DataPoint(1,0)});
            }
            series.appendData(new DataPoint(pointsPlotted, changeInAcceleration), true, pointsPlotted);
            viewport.setMaxX(pointsPlotted);
            viewport.setMinX(pointsPlotted-300);
            viewport.setMaxY(25);


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Room database
        database = RoomDB.getInstance(this,getIntent().getStringExtra("Road"));
        dataList = database.mainDao().getAll();
        database.mainDao().reset(dataList);


        txt_stepCount = findViewById(R.id.txt_distance);
        Handler handler= new Handler();
        handler.post(new Runnable() {
                         public void run() {
                             counter = counter + 1;
                             txt_stepCount.setText("Counter: " + String.valueOf(counter));
                             if(vec.size()>0){
                                 int max = Collections.max(vec);
                                 if(max>10){
                                     //txt_acceleration.setBackgroundColor(Color.RED);
                                     // key =1 value = one;
                                     //key = counter
                                     Toast.makeText(MainActivity.this, " BIG Pothole Detected at " + counter, Toast.LENGTH_LONG).show();
                                     MainData data = new MainData();
                                     data.setID(counter);
                                     data.setValue(getIntent().getStringExtra("Road") + " BIG Pothole Detected at " + counter);
                                     database.mainDao().insert(data);
                                 }
                                 else if(max>5){
                                     //txt_acceleration.setBackgroundColor(Color.parseColor("#fcad03"));
                                     Toast.makeText(MainActivity.this, "Pothole Detected at " + counter, Toast.LENGTH_LONG).show();
                                     MainData data = new MainData();
                                     data.setID(counter);
                                     data.setValue(getIntent().getStringExtra("Road") +" Pothole Detected at " + counter);
                                     database.mainDao().insert(data);
                                 }
                                 vec.clear();
                             }
                             handler.postDelayed(this::run, 1500);
                         }
                     });

        txt_acceleration = findViewById(R.id.txt_acceleration);
        txt_currentAccel = findViewById(R.id.txt_currentAccel);
        txt_prevAccel = findViewById(R.id.txt_prevAccel);

        prog_shakeMeter = findViewById(R.id.prog_shakeMeter);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //sample graph
        GraphView graph = (GraphView) findViewById(R.id.graph);
        viewport = graph.getViewport();
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        graph.addSeries(series);

    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(sensorEventListener);
    }


}