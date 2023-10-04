package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TimeActivity extends AppCompatActivity{

    TimeFragment2 fragment2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);

        Bundle extras = getIntent().getExtras();
        int id = extras.getInt("id");
        String text = extras.getString("text");
        String ampm = extras.getString("ampm");
        String hour = extras.getString("hour");
        String minute = extras.getString("minute");

        fragment2 = new TimeFragment2();
        Bundle bundle = new Bundle(5); // 전달할 데이터 개수
        bundle.putInt("id", id);
        bundle.putString("text", text);
        bundle.putString("ampm", ampm);
        bundle.putString("hour", hour);
        bundle.putString("minute", minute);
        fragment2.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.view, fragment2).commit();
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,Button1Activity.class));
        finish();
    }
}
