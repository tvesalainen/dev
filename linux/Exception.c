#include <errno.h>

#include <jni.h>


void exception(JNIEnv * env, const char* clazz, const char* message)
{
    jclass exc;
    char buf[256];

    exc = (*env)->FindClass(env, clazz);
    if (exc == NULL)
    {
        return;
    }
    if (errno != 0)
    {
        if (message != NULL)
        {
            sprintf(buf, "%d: %s: %s", errno, strerror(errno), message);
        }
        else
        {
            sprintf(buf, "%d: %s", errno, strerror(errno));
        }
        (*env)->ThrowNew(env, exc, buf);
    }
    else
    {
        (*env)->ThrowNew(env, exc, message);
    }
}



