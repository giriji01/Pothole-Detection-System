package com.example.pds;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;


public class steper extends AppCompatActivity
{
    public Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.steper);


        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(steper.this, MainActivity.class);
                intent.putExtra("Road","Road1");
                startActivity(intent);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(steper.this, DetectorActivity.class);
                intent.putExtra("Road","Road1");
                startActivity(intent);
            }
        });

        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(steper.this, MainActivity2.class);
                intent.putExtra("Road","Road1");
                startActivity(intent);
            }
        });

//        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(steper.this, MainActivity.class);
//                intent.putExtra("Road","Road2");
//                startActivity(intent);
//            }
//        });
//
//        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(steper.this, DetectorActivity.class);
//                intent.putExtra("Road","Road2");
//                startActivity(intent);
//            }
//        });



    }

}