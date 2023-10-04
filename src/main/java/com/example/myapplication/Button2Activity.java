package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Scanner;


public class Button2Activity extends AppCompatActivity {
    Button check, play, stop;
    TextView textView;


    final static int Init = 0;
    int cur_Status = Init; //현재의 상태를 저장할변수를 초기화함.

    long myBaseTime;
    long myPauseTime;


    final String[] song = new String[]{"하프 클래식", "풀벌레 소리", "테라피 소리"};
    int[][] song_time = {{1,0},{1,0},{1,0}};

    int select = -1;
    int checking = -1;// 음악을 선택하지 않았을 때

    int match = 0;

    String post_str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button2);

        textView = (TextView)findViewById(R.id.tv);
        textView.setText("00:00");

        check = (Button)findViewById(R.id.check);
        play = (Button)findViewById(R.id.play);
        stop = (Button)findViewById(R.id.stop);

        check.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(post_str.equals("play")){
                    Toast.makeText(Button2Activity.this, "재생중인 음악을 정지한 뒤에 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    check.setText("음악 선택");
                    textView.setText("00:00");
                    select = -1;
                    checking = -1;
                    showDialog();
                }
            }
        });

        playMusic();
        stopMusic();
    }

    public void showDialog() {
        AlertDialog.Builder music_radio = new AlertDialog.Builder(this);
        music_radio.setTitle("반려묘에게 들려주고 싶은 음악을 선택해주세요.");
        music_radio.setSingleChoiceItems(song, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                select = which;
            }
        });

        music_radio.setNegativeButton("선택", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    if (select == -1) {
                        Toast.makeText(Button2Activity.this, "재생할 음악을 선택해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        //코드추가하기
                        check.setText(song[select]);
                        play.setText("음악 재생");
                        checking = 1;
                    }
                }
            });

        music_radio.setPositiveButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = music_radio.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public void playMusic(){
        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(checking == -1) {
                    Toast.makeText(Button2Activity.this, "재생할 음악을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    myBaseTime = SystemClock.elapsedRealtime();
                    System.out.println(myBaseTime);
                    myTimer.sendEmptyMessage(0);
                    play.setText("음악 다시 재생");

                    post_str = "play";
                    Post p = new Post();
                    p.execute();
                }
            }
        });
    }

    public void stopMusic() {
        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checking == -1) {
                    Toast.makeText(Button2Activity.this, "재생할 음악을 선택해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    myTimer.removeMessages(0);
                    play.setText("음악 재생");

                    post_str = "stop";
                    Post p = new Post();
                    p.execute();
                }
            }
        });
    }

    private final MyHandler myTimer = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<Button2Activity> tActivity;
        public MyHandler(Button2Activity activity) {
            tActivity = new WeakReference<Button2Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Button2Activity activity = tActivity.get();
            if (activity != null) {

                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        if(match == 1)
        {
            myTimer.removeMessages(0);
            myPauseTime = SystemClock.elapsedRealtime();
            cur_Status = Init;
            post_str = "stop";
            match = 0;
        }
        else
        {
            textView.setText(getTimeOut());
            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송
            myTimer.sendEmptyMessage(0);
        }
    }

    //현재시간을 계속 구해서 출력하는 메소드
    String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now - myBaseTime;

        if((outTime / 1000 / 60) == song_time[select][0] && ((outTime / 1000) % 60) == song_time[select][1]) {
            match = 1;
        }

        String easy_outTime = String.format(Locale.getDefault(),"%02d:%02d", outTime/1000 / 60, (outTime/1000)%60);

        return easy_outTime;
    }

    /*서버로 값 전달*/
    public class Post extends AsyncTask<Void, Void, Void> {
        protected void onPreExecute() {
        }

        protected Void doInBackground(Void... params) {
            try {
                String param="";

                if(post_str.equals("play")) {
                    switch (select) {
                        case 0:
                            param = "name=" + URLEncoder.encode("하프클래식", "UTF-8");
                            break;
                        case 1:
                            param = "name=" + URLEncoder.encode("풀벌레소리", "UTF-8");
                            break;
                        case 2:
                            param = "name=" + URLEncoder.encode("테라피소리", "UTF-8");
                            break;
                    }
                }
                else if(post_str.equals("stop")){
                    param = "name=" + URLEncoder.encode("stop", "UTF-8");
                }

                URL url = new URL("http://220.66.60.211:8080/Meow/Music.jsp?" + param);
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

    public void Button0(View v){
        finish();
    }

    public void Button1(View v) {
        finish();
        startActivity(new Intent(this,Button1Activity.class));
    }

    public void Button2(View v) { }

    public void Button3(View v) {
        finish();
        startActivity(new Intent(this,Button3Activity.class));
    }

}
