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


}
