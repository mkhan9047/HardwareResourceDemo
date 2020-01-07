#include <jni.h>
#include <string>

extern "C"
{
#include "spi.h"
#include "LTC2512.h"
#include "TempSensor.h"
#include "AccelSensor.h"
}
float  x=0;
float  y=0;
float  z=0;

extern "C" JNIEXPORT jfloat
JNICALL
Java_com_ruhof_rfid_RFIDInterface_getx(
        JNIEnv *env,
        jobject /* this */)
{
    return x;
}
extern "C" JNIEXPORT jfloat

JNICALL
Java_com_ruhof_rfid_RFIDInterface_gety(
        JNIEnv *env,
        jobject /* this */)
{
    return y;
}
extern "C" JNIEXPORT jfloat

JNICALL
Java_com_ruhof_rfid_RFIDInterface_getz(
        JNIEnv *env,
        jobject /* this */)
{
    return z;
}
extern "C" JNIEXPORT jstring

JNICALL
Java_com_ruhof_rfid_RFIDInterface_initializeSPI(
        JNIEnv *env,
        jobject /* this */,jint mode) {
    int spi_fd;
    std::string hello = "Initialization of SPI";


    spiconfig test;
    test.mode = 0;
    test.bits = 32;
    test.speed = 2000*1000;
    spi_init(test);

    return env->NewStringUTF(hello.c_str());
}
extern "C" JNIEXPORT jint

JNICALL
Java_com_ruhof_rfid_RFIDInterface_closeSPI(
        JNIEnv *env,
        jobject /* this */) {
   int rtn = close_spi();
   return rtn;
}

extern "C" JNIEXPORT jfloat

JNICALL
Java_com_ruhof_rfid_RFIDInterface_ReadSPIData(
        JNIEnv *env,
jobject,jint mode /* this */) {
    spiconfig test;

    uint16_t Dfval=32;

    float val=ReadVoltageADC(3.3,&Dfval,mode);
return val;
}

extern "C" JNIEXPORT jint

JNICALL
Java_com_ruhof_rfid_RFIDInterface_openAccelSensor(
        JNIEnv *env,
        jobject /* this */) {
    LOGE("Sensor Get Class Called");
     writeEnable(1);
    struct acceleration ev= readACC();
    x = ev.x;
    y=ev.y;
    z=ev.z;
    LOGE("the G received sensor dir is %f %f %f",ev.x,ev.y,ev.z);
    return 1;
}






