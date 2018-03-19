#include "../../net_zengzhiying_JNIDemo.h"
#include "stdlib.h"
JNIEXPORT jstring JNICALL Java_net_zengzhiying_JNIDemo_testString
  (JNIEnv *env, jobject obj, jstring jstr1){
        char* cha = (char*)env->GetStringUTFChars(jstr1, NULL);
        //printf("In dll testString :%s\n", cha);
        sprintf(cha, "c++");
        jstring ret = env->NewStringUTF(cha);
        env->ReleaseStringUTFChars(jstr1, cha);
        return ret;
}
