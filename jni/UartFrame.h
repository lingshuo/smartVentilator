/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class cn_lisa_smartventilator_hardware_UartFrame */

#ifndef _Included_cn_lisa_smartventilator_hardware_UartFrame
#define _Included_cn_lisa_smartventilator_hardware_UartFrame
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     cn_lisa_smartventilator_hardware_UartFrame
 * Method:    init
 * Signature: (Ljava/lang/String;IIIB)Z
 */
JNIEXPORT jboolean JNICALL Java_cn_lisa_smartventilator_hardware_UartFrame_init
  (JNIEnv *, jobject, jstring, jint, jint, jint, jbyte);

/*
 * Class:     cn_lisa_smartventilator_hardware_UartFrame
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cn_lisa_smartventilator_hardware_UartFrame_destroy
  (JNIEnv *, jobject);

/*
 * Class:     cn_lisa_smartventilator_hardware_UartFrame
 * Method:    recv
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_cn_lisa_smartventilator_hardware_UartFrame_recv
  (JNIEnv *, jobject, jbyteArray, jint);

/*
 * Class:     cn_lisa_smartventilator_hardware_UartFrame
 * Method:    send
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_cn_lisa_smartventilator_hardware_UartFrame_send
  (JNIEnv *, jobject, jbyteArray, jint);

#ifdef __cplusplus
}
#endif
#endif
