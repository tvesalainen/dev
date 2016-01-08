#undef __cplusplus

#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>

#include "org_vesalainen_dev_FileIO.h"
#include "Exception.h"

JNIEXPORT jint JNICALL Java_org_vesalainen_dev_FileIO_open
  (JNIEnv *env, jobject obj, jbyteArray path)
{
    jbyte *sPath;
    int fd;

    sPath = (*env)->GetByteArrayElements(env, path, NULL);
    if (sPath == NULL)
    {
        EXCEPTION("GetByteArrayElements");
    }

    fd = open(sPath, O_RDWR);
    (*env)->ReleaseByteArrayElements(env, path, sPath, 0);
    if (fd < 0)
    {
        EXCEPTION(sPath);
    }
    return fd;
}

JNIEXPORT void JNICALL Java_org_vesalainen_dev_FileIO_close
  (JNIEnv *env, jobject obj, jint fd)
{
    if (close(fd) < 0)
    {
        EXCEPTIONV("close()");
    }
}