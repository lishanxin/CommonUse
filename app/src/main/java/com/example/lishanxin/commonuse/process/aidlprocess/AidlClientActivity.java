package com.example.lishanxin.commonuse.process.aidlprocess;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lishanxin.commonuse.IRemoteService;
import com.example.lishanxin.commonuse.IRemoteServiceCallback;
import com.example.lishanxin.commonuse.R;

/**
 * https://developer.android.com/guide/components/aidl
 */
public class AidlClientActivity extends AppCompatActivity {

    private boolean mBound;

    IRemoteService mIRemoteService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIRemoteService = IRemoteService.Stub.asInterface(service);
            mBound = true;
            mCallbackText.setText("Attached.");
            try {
                mIRemoteService.registerCallback(mCallback);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
            }
            Toast.makeText(AidlClientActivity.this, R.string.remote_service_connected,
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            mIRemoteService = null;
            mCallbackText.setText("Disconnected.");
            Toast.makeText(AidlClientActivity.this, R.string.remote_service_disconnected,
                    Toast.LENGTH_SHORT).show();
        }
    };

    TextView mCallbackText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl_client);

        mCallbackText = (TextView)findViewById(R.id.callback);
        mCallbackText.setText("Not attached.");

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(AidlClientActivity.this, RemoteService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        mCallbackText.setText("Binding.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    public void startAidlTest(View view) {
        try {
            String des = mIRemoteService.getStudent().getTeacher();
            mCallbackText.setText(des);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * This implementation is used to receive callbacks from the remote
     * service.
     */
    private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
        /**
         * This is called by the remote service regularly to tell us about
         * new values.  Note that IPC calls are dispatched through a thread
         * pool running in each process, so the code executing here will
         * NOT be running in our main thread like most other things -- so,
         * to update the UI, we need to use a Handler to hop over there.
         */
        public void valueChanged(int value) {
            mHandler.sendMessage(mHandler.obtainMessage(BUMP_MSG, value, 0));
        }

    };
    private static final int BUMP_MSG = 1;

    private Handler mHandler = new Handler() {
        @Override public void handleMessage(Message msg) {
            switch (msg.what) {
                case BUMP_MSG:
                    mCallbackText.setText("Received from service: " + msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

}
