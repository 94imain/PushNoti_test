package com.example.woojinkim.pushnotitest;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    Wlan1Info wlan1Info = new Wlan1Info();

    String token = FirebaseInstanceId.getInstance().getToken();

    @BindView(R.id.ae)EditText Etae;
    @BindView(R.id.cnt) EditText Etcnt;

    @BindView(R.id.save)Button buttonSave;
    @OnClick(R.id.save) void save() {
        wlan1Info.ssid = Etae.getText().toString();
        wlan1Info.pwd = Etcnt.getText().toString();
        String json = "{\"ae\":\""+wlan1Info.ssid+"\"" +
                ",\"cnt\":\""+wlan1Info.pwd+"\"" +
                ",\"fcm_token\":\""+token+"\"}";

        MySaveTask mySaveTask = new MySaveTask(
                "10.0.0.1",
                8082,json);
        mySaveTask.execute();
    }
    @OnClick(R.id.next) void next() {
        Intent intent = new Intent(this, NextActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d("token",FirebaseInstanceId.getInstance().getToken());
        Log.d("length", ""+token.length());
    }

    public class MySaveTask extends AsyncTask<Void, Void, Void> {

        String dstAddress;
        int dstPort;
        String response = "";

        String jsonString;

        MySaveTask(String addr, int port,String json){
            dstAddress = addr;
            dstPort = port;
            jsonString=json;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(dstAddress, dstPort);

                ByteArrayOutputStream byteArrayOutputStream =
                        new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];

                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                outputStream.write(jsonString.getBytes());
    /*
     * notice:
     * inputStream.read() will block if no data return
     */
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

    }

}