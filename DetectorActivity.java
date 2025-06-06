package com.example.pds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetectorActivity extends AppCompatActivity {

    List<MainData> dataList = new ArrayList<>();
    RoomDB database;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detector);

        dataList.clear();
        Toast.makeText(this, getIntent().getStringExtra("Road"), Toast.LENGTH_SHORT).show();
        database = RoomDB.getInstance(this,getIntent().getStringExtra("Road"));
        dataList = database.mainDao().getAll();

        TextView textView = findViewById(R.id.text);
        TextView step = findViewById(R.id.step);

        for (int i=0;i<dataList.size();i++) {
            if (dataList.get(i).getValue().charAt(4) == getIntent().getStringExtra("Road").charAt(4)) {
                textView.setText(textView.getText() + "\n" + dataList.get(i).getValue());
            }
        }

//        Handler handler= new Handler();
//        handler.post(new Runnable() {
//            public void run() {
//                if (counter<dataList.size()) {
//                    counter = counter + 1;
//                    int i = counter + 2;
//                    step.setText("Steps: " + String.valueOf(counter));
//                    if (dataList.get(i).getValue().charAt(4) == getIntent().getStringExtra("Road").charAt(4)) {
//                        textView.setText(dataList.get(i).getValue());
//                    }
//                    handler.postDelayed(this::run, 1000);
//                }
//            }
//        });

//        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                findViewById(R.id.start).setVisibility(View.GONE);
//            }
//        });





    }
}