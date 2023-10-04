package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class TimeFragment extends Fragment {
    private static final String TAG = "TimeFragment";

    ViewGroup rootView;

    /*아이템을 띄울 변수*/
    RecyclerView recyclerView;
    ItemAdapter adapter;

    Context context;
    OnTabItemSelectedListener listener;

    DatabaseHelper dbHelper;
    SQLiteDatabase database;

    public SharedPreferences prefs;
    Context mContext;

    /*현재 시간을 구할 변수*/
    TextView textView;

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
        rootView = (ViewGroup) inflater.inflate(R.layout.timefragment, container, false);
        textView = (TextView) rootView.findViewById(R.id.textview);

        SetTime();

        initUI(rootView);

        return rootView;
    }

    ArrayList<List> items = new ArrayList<List>();

    private void initUI(ViewGroup rootView) {

        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ItemAdapter();

        createDatabase();

        this.mContext = context;
        prefs = mContext.getSharedPreferences("Pref", Context.MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun",true);
        if(isFirstRun)//초기 데이터 값 설정
        {
            createTable();
            insertRecord();
            prefs.edit().putBoolean("isFirstRun",false).apply();
        }

        executeQuery(adapter);
        adapter.setItems(items);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(ItemAdapter.ViewHolder holder, View view, int position) {
                List item = adapter.getItem(position);

                getActivity().finish();
                listener.showFragment2(item);
            }
        });
    }

    private void createDatabase() {
        dbHelper = new DatabaseHelper(getActivity());
        database = dbHelper.getWritableDatabase();
    }

    private void createTable() {
        database.execSQL("create table if not exists " + DatabaseHelper.TABLE_ITEM + "("
                + "_id integer PRIMARY KEY autoincrement, "
                + "text text, "
                + "ampm text, "
                + "hour text, "
                + "minute text)");
    }

    private void insertRecord(){
        database.execSQL("insert into " + DatabaseHelper.TABLE_ITEM
                + "(text, ampm, hour, minute) "
                + " values "
                + "('아침배식', '오전', '7', '00')");
        database.execSQL("insert into " + DatabaseHelper.TABLE_ITEM
                + "(text, ampm, hour, minute) "
                + " values "
                + "('점심배식', '오후', '12', '00')");
        database.execSQL("insert into " + DatabaseHelper.TABLE_ITEM
                + "(text, ampm, hour, minute) "
                + " values "
                + "('저녁배식', '오후', '6', '00')");
    }

    public void executeQuery(ItemAdapter adapter) {
        if (database != null) {
            Cursor cursor = database.rawQuery("select _id, text, ampm, hour, minute from meow", null);
            int recordCount = cursor.getCount();

            for (int i = 0; i < recordCount; i++) {
                cursor.moveToNext();

                int id = cursor.getInt(0);
                String text = cursor.getString(1);
                String ampm = cursor.getString(2);
                String hour = cursor.getString(3);
                String minute = cursor.getString(4);

                items.add(new List(id, text, ampm, hour, minute));
             }
            cursor.close();

            adapter.notifyDataSetChanged();
        }
    }

    public void SetTime(){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){

                long now = System.currentTimeMillis(); // 현재 시간
                Date date = new Date(now);
                SimpleDateFormat CurHourFormat = new SimpleDateFormat("HH");
                SimpleDateFormat CurMinuteFormat = new SimpleDateFormat("mm");
                String strCurHour = CurHourFormat.format(date);//시
                String strCurMinute = CurMinuteFormat.format(date);//분

                CalculationTime(strCurHour,strCurMinute);
            }
        };

        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true){
                    try{
                        Thread.sleep(100);
                    }catch (InterruptedException e){}

                    handler.sendEmptyMessage(1);//시간 최신화
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
    /*다음 배식 시간 구하기 - 저장된 시간을 구해서 현재 시간과 비교*/
    public void CalculationTime(String strCurHour, String strCurMinute) {
        int now_hour = Integer.parseInt(strCurHour);
        int now_minute = Integer.parseInt(strCurMinute);
        int int_hour[] = new int[3];
        int int_minute[] = new int[3];
        int temp_hour[] = new int[3];
        int temp_minute[] = new int[3];
        int get_hour, get_minute;

        if (database != null) {
            Cursor cursor = database.rawQuery("select _id, text, ampm, hour, minute from meow", null);
            int recordCount = cursor.getCount();

            for (int i = 0; i < recordCount; i++) {
                cursor.moveToNext();

                String ampm = cursor.getString(2);
                String hour = cursor.getString(3);
                String minute = cursor.getString(4);

                int_hour[i] = Integer.parseInt(hour);
                int_minute[i] = Integer.parseInt(minute);
                if (ampm.equals("오후")) {
                    if(int_hour[i] != 12)
                       int_hour[i] = int_hour[i] + 12;
                }
            }
            cursor.close();
        }

        for(int i = 0 ; i < 3 ; i++){
            if(now_hour > int_hour[i])
            {
                int_hour[i] += 24;
            }
            if(now_minute > int_minute[i])
            {
                int_hour[i]--;
                int_minute[i] = int_minute[i] + 60;

            }

            temp_hour[i] = int_hour[i] - now_hour;
            temp_minute[i] = int_minute[i] - now_minute;

            if(temp_hour[i] < 0)
            {
                temp_hour[i] += 24;
            }
            if(temp_hour[i] == 0 && temp_minute[i] == 0)
            {
                temp_hour[i] = 24;
            }
        }

        get_hour = temp_hour[0];
        get_minute = temp_minute[0];
        for(int i = 1 ; i < 3 ; i++) {
            if(get_hour > temp_hour[i]) {
                get_hour = temp_hour[i];
                get_minute = temp_minute[i];
            }
            else if(get_hour == temp_hour[i]) {
                if(get_minute > temp_minute[i]){
                    get_hour = temp_hour[i];
                    get_minute = temp_minute[i];
                }
            }
        }

        textView.setText(get_hour + "시 " + get_minute + "분 후에 배식이 시작됩니다.");
     }
}
