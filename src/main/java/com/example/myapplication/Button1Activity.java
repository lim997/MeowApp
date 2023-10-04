package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Button1Activity extends AppCompatActivity implements OnTabItemSelectedListener {
    private static final String TAG = "Button1Activity";

    TimeFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button1);

        fragment = new TimeFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();

    }

    public void showFragment2(List item) {
        Intent intent = new Intent(Button1Activity.this, TimeActivity.class);
        intent.putExtra("id", item.get_id());
        intent.putExtra("text", item.gettext());
        intent.putExtra("ampm", item.getampm());
        intent.putExtra("hour", item.gethour());
        intent.putExtra("minute", item.getminute());

        startActivity(intent);
    }
    public void Button0(View v){
        finish();
    }

    public void Button1(View v) {    }

    public void Button2(View v) {
        finish();
        startActivity(new Intent(this,Button2Activity.class));
    }

    public void Button3(View v) {
        finish();
        startActivity(new Intent(this,Button3Activity.class));
    }
}
