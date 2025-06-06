package com.example.pds;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    int counter = 0;
    int i = 2;
    List<MainData> dataList = new ArrayList<>();
    RoomDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_temps);

        dataList.clear();
        Toast.makeText(this, getIntent().getStringExtra("Road"), Toast.LENGTH_SHORT).show();
        database = RoomDB.getInstance(this,getIntent().getStringExtra("Road"));
        dataList = database.mainDao().getAll();

        TextView step = findViewById(R.id.step);
        TextView text = findViewById(R.id.text);

        Handler handler= new Handler();
        handler.post(new Runnable() {
            public void run() {
                dialog.dismiss();
                    counter = counter+ 1;
                    step.setText("Counter: " + String.valueOf(counter));
                    i = i+1;
                    for (int c=0;c<dataList.size();c++) {
                        if (dataList.get(c).getID() == i) {
                            dialog.show();
                            text.setText(dataList.get(c).getValue()+"\nSlow Your Speed,\n Pothole with in few step.");
                        }
                    }
                    handler.postDelayed(this::run, 1500);

            }
        });
        Handler close= new Handler();
        close.post(new Runnable() {
            public void run() {
                dialog.dismiss();
                close.postDelayed(this::run, 3000);

            }
        });
    }
}