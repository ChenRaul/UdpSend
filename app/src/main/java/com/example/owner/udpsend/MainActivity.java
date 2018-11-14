package com.example.owner.udpsend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private DatagramSocket socket;
    private InetAddress address;
    private String[] data = {"哈哈哈哈上岁数","sssss","YAYAYAYY","我是中国人sssss","CoOchdhddh"};
    private int length = 5;
    private int index=0;
    public Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, (CharSequence) msg.obj,1).show();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            socket = new DatagramSocket(32222);
            address = InetAddress.getByName("192.168.50.222");
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(index++ > 4){
                            index = 0;
                        }
                        send(index);


                    }
                }).start();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Integer port = 12345;
                // 接收的字节大小，客户端发送的数据不能超过这个大小
                byte[] message = new byte[1024];
                try {
                    // 建立Socket连接
                    DatagramSocket datagramSocket = new DatagramSocket(port);
                    DatagramPacket datagramPacket = new DatagramPacket(message,
                            message.length);
                    DatagramPacket p = null;

                    try {
                        while (true) {
                            // 准备接收数据
                            datagramSocket.receive(datagramPacket);
                            Log.d("UDP Demo", datagramPacket.getAddress()
                                    .getHostAddress().toString()
                                    + ":" + new String(datagramPacket.getData()));

                            Message msg = handler.obtainMessage();
                            msg.obj=new String(datagramPacket.getData());
                            handler.sendMessage(msg);

                            String str = data[0];
                            try {
                                p = new DatagramPacket(str.getBytes("UTF-8"),str.getBytes("UTF-8").length,address,3999);
                                datagramSocket.send(p);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void send(int index){
        String str = data[index];
        DatagramPacket p = null;
        try {
            p = new DatagramPacket(str.getBytes("UTF-8"),str.getBytes("UTF-8").length,address,3999);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            socket.send(p);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"发送成功",1).show();
                }
            });
        } catch (IOException e) {
           final  String msg = e.getMessage();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,"发送失败："+msg,1).show();
                }
            });
            e.printStackTrace();
        }
    }

}
