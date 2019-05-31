//
// Created by Administrator on 2019/5/31.
//

#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_lishanxin_commonuse_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    /*
    std::string hello = "Hello from C++ 2法师打发";
    return env->NewStringUTF(hello.c_str());
    */
    char buf[128]="hahha";
    return env->NewStringUTF( buf);

}

