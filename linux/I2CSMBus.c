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
#include <linux/i2c-dev.h>

#include "org_vesalainen_dev_i2c_I2CSMBus.h"
#include "Exception.h"

JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_writeQuick
  (JNIEnv *env, jobject obj, jint fd, jboolean bit)
{
    if (i2c_smbus_write_quick(fd, bit) < 0)
    {
        EXCEPTIONV("writeQuick(0x%x)", bit);
    }
}
JNIEXPORT jbyte JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_readByte
  (JNIEnv *env, jobject obj, jint fd)
{
    int rc;
    rc = i2c_smbus_read_byte(fd);
    if (rc < 0)
    {
        EXCEPTION("readByte()");
    }
    return rc;
}
JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_writeByte
  (JNIEnv *env, jobject obj, jint fd, jbyte b)
{
    if (i2c_smbus_write_byte(fd, b) < 0)
    {
        EXCEPTIONV("writeByte(0x%x)", b);
    }
}
JNIEXPORT jbyte JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_readByteData
  (JNIEnv *env, jobject obj, jint fd, jbyte comm)
{
    int rc;
    rc = i2c_smbus_read_byte_data(fd, comm);
    if (rc < 0)
    {
        EXCEPTION("readByteData()");
    }
    return rc;
}
JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_writeByteData
  (JNIEnv *env, jobject obj, jint fd, jbyte comm, jbyte b)
{
    if (i2c_smbus_write_byte_data(fd, comm, b) < 0)
    {
        EXCEPTIONV("writeByteData(0x%x)", b);
    }
}
JNIEXPORT jshort JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_readWordData
  (JNIEnv *env, jobject obj, jint fd, jbyte comm)
{
    int rc;
    rc = i2c_smbus_read_word_data(fd, comm);
    if (rc < 0)
    {
        EXCEPTION("readWordData()");
    }
    return rc;
}
JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_writeWordData
  (JNIEnv *env, jobject obj, jint fd, jbyte comm, jshort w)
{
    if (i2c_smbus_write_word_data(fd, comm, w) < 0)
    {
        EXCEPTIONV("writeWordData(0x%x)", w);
    }
}
JNIEXPORT jint JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_readBlockData
  (JNIEnv *env, jobject obj, jint fd, jbyte comm, jbyteArray buf, jint off, jint len)
{
    jbyte *sBuf;
    int rc;

    sBuf = (*env)->GetByteArrayElements(env, buf, NULL);
    if (sBuf == NULL)
    {
        EXCEPTION("GetByteArrayElements");
    }

    rc = i2c_smbus_read_block_data(fd, comm, sBuf+off);
    if (rc < 0)
    {
        (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
        EXCEPTION("readBlockData()");
    }
    (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
    return rc;
}
JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_writeBlockData
  (JNIEnv *env, jobject obj, jint fd, jbyte comm, jbyteArray buf, jint off, jint len)
{
    jbyte *sBuf;

    sBuf = (*env)->GetByteArrayElements(env, buf, NULL);
    if (sBuf == NULL)
    {
        EXCEPTIONV("GetByteArrayElements");
    }

    if (i2c_smbus_write_block_data(fd, comm, len, sBuf+off) < 0)
    {
        (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
        EXCEPTIONV("writeBlockData()");
    }
    (*env)->ReleaseByteArrayElements(env, buf, sBuf, 0);
}
JNIEXPORT jshort JNICALL Java_org_vesalainen_dev_i2c_I2CSMBus_processCall
  (JNIEnv *env, jobject obj, jint fd, jbyte comm, jshort w)
{
    int rc;
    rc = i2c_smbus_process_call(fd, comm, w);
    if (rc < 0)
    {
        EXCEPTION("processCall()");
    }
    return rc;
}
