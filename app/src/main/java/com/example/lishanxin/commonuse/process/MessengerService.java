package com.example.lishanxin.commonuse.process;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.widget.Toast;


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
                    Toast.makeText(getApplicationContext(), "hello, trmpcr", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    Messenger mMessenger = new Messenger(new IncomingHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }
}
