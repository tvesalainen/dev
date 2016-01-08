/*
 * Copyright (C) 2016 Timo Vesalainen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
#undef __cplusplus

#include <limits.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>

#include "org_vesalainen_dev_FileIO.h"
#include "Exception.h"

JNIEXPORT jint JNICALL Java_org_vesalainen_dev_FileIO_open
  (JNIEnv *env, jobject obj, jbyteArray path)
{
    jbyte *sPath;
    jsize size;
    char szPath[PATH_MAX];
    int fd;

    sPath = (*env)->GetByteArrayElements(env, path, NULL);
    if (sPath == NULL)
    {
        EXCEPTION("GetByteArrayElements");
    }

    size = (*env)->GetArrayLength(env, path);
    strncpy(szPath, sPath, size);
    
    fd = open(szPath, O_RDWR);
    (*env)->ReleaseByteArrayElements(env, path, sPath, 0);
    if (fd < 0)
    {
        EXCEPTION(szPath);
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

JNIEXPORT jint JNICALL Java_org_vesalainen_dev_FileIO_read__I
  (JNIEnv *env, jobject obj, jint fd)
{
    char buf[1];
    int rc;
    
    if (read(fd, buf, 1) != 1)
    {
        EXCEPTION("read()");
    }
    return buf[0];
}

JNIEXPORT void JNICALL Java_org_vesalainen_dev_FileIO_write__II
  (JNIEnv *env, jobject obj, jint fd, jint cc)
{
    char buf[1];

    buf[0] = cc;
    if (write(fd, buf, 1) != 1)
    {
        EXCEPTIONV("write()");
    }
}

JNIEXPORT jint JNICALL Java_org_vesalainen_dev_FileIO_read__I_3B
  (JNIEnv *env, jobject obj, jint fd, jbyteArray buf)
{
    jbyte *sBuf;
    jsize size;
    int rc;

    sBuf = (*env)->GetByteArrayElements(env, buf, NULL);
    if (sBuf == NULL)
    {
        EXCEPTION("GetByteArrayElements");
    }
    size = (*env)->GetArrayLength(env, buf);

    rc = read(fd, sBuf, size);
    if (rc < 0)
    {
        (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
        EXCEPTION("read()");
    }
    (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
    return rc;
}

JNIEXPORT void JNICALL Java_org_vesalainen_dev_FileIO_write__I_3BII
  (JNIEnv *env, jobject obj, jint fd, jbyteArray buf, jint start, jint length)
{
    jbyte *sBuf;

    sBuf = (*env)->GetByteArrayElements(env, buf, NULL);
    if (sBuf == NULL)
    {
        EXCEPTIONV("GetByteArrayElements");
    }

    if (write(fd, sBuf+start, length) != length)
    {
        (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
        EXCEPTIONV("write()");
    }
    (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
}
