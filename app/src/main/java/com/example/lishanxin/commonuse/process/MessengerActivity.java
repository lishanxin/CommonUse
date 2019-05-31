package com.example.lishanxin.commonuse.process;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.lishanxin.commonuse.IRemoteService;
import com.example.lishanxin.commonuse.R;
import com.example.lishanxin.commonuse.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * https://developer.android.com/guide/components/bound-services.html#Binder
 */
public class MessengerActivity extends AppCompatActivity {

    private boolean mBound;
    private Messenger mMessenger;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mMessenger = new Messenger(service);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMessenger = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
    }

    public void sayHello(View v){
        if (!mBound)
            return;
        Message msg = Message.obtain(null, 0,0,0);
        Bundle bundle = new Bundle();
        bundle.putString("KEY_ADDRESS", "I am from Activity");
        bundle.putParcelable("PARCELABLE1", new Student("lee Bundle", "chen Bundle"));
        msg.setData(bundle);
        try {
            mMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(MessengerActivity.this, MessengerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
    }


    public void socketTest(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //IO操作不能放在主线程执行
                connectServer();
            }
        }).start();
    }

    private static void connectServer() {
        //https://blog.csdn.net/hzw2017/article/details/81210979
        try {
            //1、创建客户端socket，指定服务端地址和端口
            Socket socket = new Socket("localhost", 8888);
            boolean connected = socket.isConnected(); //检查客户端与服务端是否连接成功
            System.out.println(connected?"连接成功":"连接失败，请重试！");

            //2、获取输出流，向服务器发送消息
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream),true);
            writer.write("第一次来到广州\n");
            writer.flush();

            //3、获取输入流，并读取服务端的响应信息
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String info=reader.readLine();
            System.out.println("客户端收到服务端回应："+info);


            //4、关闭资源
            outputStream.close();
            writer.close();
            inputStream.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
