package com.example.lishanxin.commonuse.process;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * @author: Li Shanxin
 * @date: 2019/4/14
 * @description:
 */

public class MessengerService extends Service {

    class IncomingHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    Toast.makeText(getApplicationContext(), "hello, trmpcr" + msg.getData().getString("KEY_ADDRESS") + msg.getData().getParcelable("PARCELABLE1").toString(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                serviceSocket();
            }
        }).start();
        return mMessenger.getBinder();
    }


    private void serviceSocket(){
        try {
            //1、创建ServerSocket对象，指定与客户端一样的端口号
            ServerSocket serverSocket = new ServerSocket(8888);
            //2、获取Socket实例
            final Socket socket = serverSocket.accept();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //3、获取输入流，接受客户端发来的消息
                        InputStream inputStream = socket.getInputStream();
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader reader = new BufferedReader(inputStreamReader);
                        String info=reader.readLine();
                        System.out.println("服务端收到客户端的信息： " +info);

                        //4、获取输出流，向客户端发送消息回应
                        OutputStream outputStream = socket.getOutputStream();
                        PrintWriter writer = new PrintWriter(outputStream);
                        writer.write("羊城欢迎你！"+"\n");
                        writer.flush();

                        //4、关闭IO资源
                        inputStream.close();
                        reader.close();
                        outputStream.close();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
