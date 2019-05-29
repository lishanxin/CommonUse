package com.example.lishanxin.commonuse.process.aidlprocess;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lishanxin.commonuse.IRemoteService;
import com.example.lishanxin.commonuse.IRemoteServiceCallback;
import com.example.lishanxin.commonuse.Student;


/**
 * @author: Li Shanxin
 * @date: 2019/5/29
 * @description:
 */

public class RemoteService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {
        @Override
        public int getPid() throws RemoteException {
            return Process.myPid();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            Log.d("Stub AIDL", "HAHA");
        }

        @Override
        public void registerCallback(IRemoteServiceCallback mCallback) throws RemoteException {
            mCallback.valueChanged(300);
        }

        @Override
        public Student getStudent() throws RemoteException {
            return new Student("haha", "alskdjflaskd");
        }
    };
}
