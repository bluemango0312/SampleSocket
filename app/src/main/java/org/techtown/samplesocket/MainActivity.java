package org.techtown.samplesocket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    EditText editText;

    TextView textView;
    TextView textView2;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editTextTextPersonName);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String data = editText.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(data);
                    }
                }).start();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                }).start();
            }
        });

    }

    public void printClientLog(final String data){
        Log.d("MainActivity", data);
        handler.post(new Runnable() { //클라이언트 쪽 로그를 화면에 있는 텍스트 뷰에 출력하기위해 핸들러 사용
            @Override
            public void run() {
                textView.append(data + "\n");
            }
        });
    }

    public void printServerLog(final String data){
        Log.d("MainActivity", data);
        handler.post(new Runnable() { //서버 쪽 로그를 화면에 있는 텍스트 뷰에 출력하기위해 핸들러 사용
            @Override
            public void run() {
                textView2.append(data + "\n");
            }
        });
    }

    public void send(String data){
        try{
            int portNumber = 5001;
            Socket sock = new Socket("localhost", portNumber); //소켓 객체 만들기
            printClientLog("소켓 연결함.");

            ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream()); //소켓 객체로 데이터 보내기
            outstream.writeObject(data);
            outstream.flush();
            printClientLog("데이터 전송함.");

            ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
            printClientLog("서버로부터 받음 : " + instream.readObject());
            sock.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void startServer(){
        try{
            int portNumber = 5001;

            ServerSocket server = new ServerSocket(portNumber); //소켓 서버 객체 만들기
            printServerLog("서버 시작함 : " + portNumber);

            while (true){
                Socket sock = server.accept(); //클라이언트가 접속했을 때 만들어지는 소켓 객체 참조하기
                InetAddress clientHost = sock.getLocalAddress();
                int clientPort = sock.getPort();
                printServerLog("클라이언트 연결됨 : " + clientHost + " : " + clientPort);

                ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
                Object obj = instream.readObject();
                printServerLog("데이터 받음 : "+obj);

                ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());
                outstream.writeObject(obj + "from Server.");
                outstream.flush();
                printServerLog("데이터 보냄.");

                sock.close();

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

