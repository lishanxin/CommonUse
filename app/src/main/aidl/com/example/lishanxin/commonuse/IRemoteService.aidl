// IRemoteService.aidl
package com.example.lishanxin.commonuse;

// Declare any non-default types here with import statements
import com.example.lishanxin.commonuse.IRemoteServiceCallback;
import com.example.lishanxin.commonuse.Student;
interface IRemoteService {

    int getPid();
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

              void registerCallback(IRemoteServiceCallback mCallback);

              Student getStudent();
}
