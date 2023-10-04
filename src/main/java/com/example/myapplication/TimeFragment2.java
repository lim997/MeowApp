package com.example.myapplication;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Scanner;

public class TimeFragment2 extends Fragment {
    private static final String TAG = "TimeFragment2";

    Context context;
    OnTabItemSelectedListener listener;

    int id;
    String text;
    String ampm;
    String hour;
    String minute;

    int hour_int;
    int minute_int;

    EditText contentsInput;

    TimePicker timePicker;
    Calendar calendar;

    String ap;
    int h;
    String str_h, str_m;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;
    ItemAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.context = context;

        if (context instanceof OnTabItemSelectedListener) {
            listener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (context != null) {
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.timefragment2, container, false);

        id = getArguments().getInt("id");
        text = getArguments().getString("text");
        ampm = getArguments().getString("ampm");
        hour = getArguments().getString("hour");
        minute = getArguments().getString("minute");

        hour_int = Integer.parseInt(hour);
        minute_int = Integer.parseInt(minute);

        createDatabase();
        initUI(rootView);

        return rootView;
    }

    private void initUI(final ViewGroup rootView) {
        adapter = new ItemAdapter();

        calendar = Calendar.getInstance();

        timePicker = (TimePicker) rootView.findViewById(R.id.timepicker);
        contentsInput = rootView.findViewById(R.id.textview);

        if (ampm.equals("오후")) {
            if(hour_int != 12)
               hour_int = hour_int + 12;
        }
        setTimePicker();
        setContents();

        Button saveButton = rootView.findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTime();
                UpdateList(adapter);

                Bubble_sort();

                Post p = new Post();
                p.execute();

                startActivity(new Intent(getActivity(),Button1Activity.class));
                getActivity().finish();
            }
        });

        Button closeButton = rootView.findViewById(R.id.close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),Button1Activity.class));
                getActivity().finish();
            }
        });
    }

    private void createDatabase() {
        dbHelper = new DatabaseHelper(getActivity());
        database = dbHelper.getWritableDatabase();
    }

    // 데이터베이스 레코드 수정
    private void UpdateList(ItemAdapter adapter) {
        String sql = "update " + DatabaseHelper.TABLE_ITEM +
                " set " +
                " text = '" + contentsInput.getText() + "'" +
                ", ampm = '" + ap + "'" +
                ", hour = '" + str_h + "'" +
                ", minute = '" + str_m + "'" +
                " where " +
                " _id = " + id;

        Log.d(TAG, "sql : " + sql);
        database.execSQL(sql);

        adapter.notifyDataSetChanged();

        Toast.makeText(getContext(), contentsInput.getText()+"이 "+ ap +" "+ str_h +"시 "+ str_m + "분으로 변경되었습니다." , Toast.LENGTH_LONG).show();
    }

    public void Bubble_sort(){
        int[][] temp = new int[3][3];
        String[][] str_temp = new String[3][2];
        int[] change = new int[3];
        String[] str_change = new String[2];
        int count = 0;
        int i ,j, k;

        if (database != null) {
            Cursor cursor = database.rawQuery("select _id, text, ampm, hour, minute from meow", null);
            int recordCount = cursor.getCount();
            count = recordCount;

            for (i = 0; i < recordCount; i++) {
                cursor.moveToNext();

                temp[i][0] = cursor.getInt(0);
                str_temp[i][0] = cursor.getString(1);
                str_temp[i][1] = cursor.getString(2);
                temp[i][1] = Integer.parseInt(cursor.getString(3));
                temp[i][2] = Integer.parseInt(cursor.getString(4));

                if (str_temp[i][1].equals("오후")) {
                    if(temp[i][1] != 12)
                        temp[i][1] = temp[i][1] + 12;
                }
            }
            cursor.close();
        }

        for(i = 0 ; i < count - 1 ; i++){
            for (j = 0 ; j < count - i - 1 ; j++)
            {
                if((temp[j][1] > temp[j+1][1]) || ((temp[j][1] == temp[j+1][1])&&(temp[j][2] > temp[j+1][2]))) {
                    for(k=0;k<3;k++) {
                        change[k] = temp[j][k];
                        temp[j][k] = temp[j + 1][k];
                        temp[j + 1][k] = change[k];
                    }
                    for(k=0;k<2;k++) {
                        str_change[k] = str_temp[j][k];
                        str_temp[j][k] = str_temp[j + 1][k];
                        str_temp[j + 1][k] = str_change[k];
                    }
                }
            }
        }

        /*정렬된 값 넣기*/
        if (database != null) {
            Cursor cursor = database.rawQuery("select _id, text, ampm, hour, minute from meow", null);
            int recordCount = cursor.getCount();
            String[] str_time = new String[2];
            int c = 1;

            for (i = 0; i < recordCount; i++) {
                cursor.moveToNext();

                if(str_temp[i][1].equals("오후")){
                    if(temp[i][1] != 12)
                        temp[i][1] = temp[i][1] - 12;
                }
                str_time[0] = String.valueOf(temp[i][1]);
                str_time[1] = String.valueOf(temp[i][2]);
                str_time[1] = changeMinute(str_time[1]);

                String sql = "update " + DatabaseHelper.TABLE_ITEM +
                        " set " +
                        " text = '" + str_temp[i][0] + "'" +
                        ", ampm = '" + str_temp[i][1] + "'" +
                        ", hour = '" + str_time[0] + "'" +
                        ", minute = '" + str_time[1] + "'" +
                        " where " +
                        " _id = " + c;

                Log.d(TAG, "sql : " + sql);
                database.execSQL(sql);
                c++;
            }
            cursor.close();

            adapter.notifyDataSetChanged();
        }
    }

    /*서버로 값 전달*/
    public class Post extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
        }

        protected Void doInBackground(Void... params) {
            try {
                String param;
                String id_1="", id_2="", id_3="";

                if (database != null) {
                    Cursor cursor = database.rawQuery("select _id, text, ampm, hour, minute from meow", null);
                    int recordCount = cursor.getCount();

                    for (int i = 0; i < recordCount; i++) {
                        cursor.moveToNext();

                        int id = cursor.getInt(0);
                        String ampm = cursor.getString(2);
                        String hour = cursor.getString(3);
                        String minute = cursor.getString(4);

                        String change_ampm = "";
                        if(ampm.equals("오전")){
                            change_ampm = "am";
                        }
                        else{
                            change_ampm = "pm";
                        }
                        if (id == 1) {
                            id_1 = "f_sp=" + URLEncoder.encode(change_ampm, "UTF-8")
                                    + "&f_hour=" + URLEncoder.encode(hour, "UTF-8")
                                    + "&f_min=" + URLEncoder.encode(minute, "UTF-8");
                        }
                        else if(id == 2) {
                            id_2 = "&s_sp=" + URLEncoder.encode(change_ampm, "UTF-8")
                                    + "&s_hour=" + URLEncoder.encode(hour, "UTF-8")
                                    + "&s_min=" + URLEncoder.encode(minute, "UTF-8");
                        }
                        else if(id == 3) {
                            id_3 = "&t_sp=" + URLEncoder.encode(change_ampm, "UTF-8")
                                    + "&t_hour=" + URLEncoder.encode(hour, "UTF-8")
                                    + "&t_min=" + URLEncoder.encode(minute, "UTF-8");
                        }
                    }
                    cursor.close();

                    adapter.notifyDataSetChanged();
                }
                param = id_1 + id_2 + id_3;

                URL url = new URL("http://220.66.60.211:8080/Meow/Time.jsp?" + param);
                URLConnection conn = url.openConnection();
                conn.setUseCaches(false);

                InputStream is = conn.getInputStream();
                Scanner scan = new Scanner(is);

                int line = 1;
                while (scan.hasNext()) {
                    String str = scan.nextLine();
                    System.out.println((line++) + ":" + str);
                }
                scan.close();
            } catch (MalformedURLException e) {
                System.out.println("The URL address is incorrect");
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println("It can't connect to the web page");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
        }
    }

    public void getTime(){
        h = timePicker.getHour();
        if (h >= 0 && h < 12) {
            ap = "오전";
        }
        else if(h == 12){
            ap = "오후";
        }
        else {
            h = h - 12;
            ap = "오후";
        }
        str_h = String.valueOf(h);
        str_m = String.valueOf(timePicker.getMinute());
        switch (str_m) {
            case "0":
                str_m = "00";
                break;
            case "1":
                str_m = "01";
                break;
            case "2":
                str_m = "02";
                break;
            case "3":
                str_m = "03";
                break;
            case "4":
                str_m = "04";
                break;
            case "5":
                str_m = "05";
                break;
            case "6":
                str_m = "06";
                break;
            case "7":
                str_m = "07";
                break;
            case "8":
                str_m = "08";
                break;
            case "9":
                str_m = "09";
                break;
        }
    }

    public void setContents() {
        contentsInput.setText(text);
    }

    public void setTimePicker() {
        timePicker.setHour(hour_int);
        timePicker.setMinute(minute_int);
    }

    public String changeMinute(String t) {
        switch (t) {
            case "0":
                t = "00";
                break;
            case "1":
                t = "01";
                break;
            case "2":
                t = "02";
                break;
            case "3":
                t = "03";
                break;
            case "4":
                t = "04";
                break;
            case "5":
                t = "05";
                break;
            case "6":
                t = "06";
                break;
            case "7":
                t = "07";
                break;
            case "8":
                t = "08";
                break;
            case "9":
                t = "09";
                break;
        }
        return t;
    }
}