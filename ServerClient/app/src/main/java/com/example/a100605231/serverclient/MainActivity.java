package com.example.a100605231.serverclient;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Handler UIHandler;
    Thread Thread1 = null;

    private EditText EDITTEXT;

    public static final int SERVERPORT = 5005;
    public static final String SERVERIP = "10.10.124.243";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EDITTEXT = (EditText) findViewById(R.id.edittext);

        UIHandler = new Handler();

        this.Thread1 = new Thread(new Thread1());
        this.Thread1.start();
    }

    class Thread1 implements Runnable{
        public void run() {
            Socket socket = null;

                try{
                    InetAddress serverAddr = InetAddress.getByName(SERVERIP);
                    socket = new Socket(serverAddr, SERVERPORT);

                    Thread2 commThread = new Thread2(socket);
                    new Thread(commThread).start();
                    return;
                }catch (IOException e){
                    e.printStackTrace();
                }
        }
    }

    class Thread2 implements Runnable{
        private Socket clientSocket;
        private BufferedReader input;

        public Thread2(Socket clientSocket) {
            this.clientSocket = clientSocket;

            try{
                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            while (!Thread.currentThread().isInterrupted()){

                try{
                    String read = input.readLine();
                    if(read != null){
                        UIHandler.post(new updateUIThread(read));
                    }
                    else{
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class updateUIThread implements Runnable{
        private String msg;

        public updateUIThread(String str) {this.msg = str;}

        @Override
        public void run(){
            EDITTEXT.setText(EDITTEXT.getText().toString()+"Server says: "+msg+ "\n" );
        }
    }


}
