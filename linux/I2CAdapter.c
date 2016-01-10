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

#include "org_vesalainen_dev_i2c_I2CAdapter.h"
#include "Exception.h"

JNIEXPORT jint JNICALL Java_org_vesalainen_dev_i2c_I2CAdapter_openAdapter
  (JNIEnv *env, jclass cls, jint adapter)
{
    int fd;
    char filename[PATH_MAX];
    
    snprintf(filename, sizeof(filename), "/dev/i2c-%d", adapter);
    fd = open(filename, O_RDWR);
    if (fd < 0)
    {
        EXCEPTION("open(%s)", filename);
    }
    return fd;
}

JNIEXPORT jlong JNICALL Java_org_vesalainen_dev_i2c_I2CAdapter_functionality
  (JNIEnv *env, jobject obj, jint fd)
{
    jlong res = 0;
    unsigned long funcs;
    int shift = 0;
    if  (ioctl(fd, I2C_FUNCS, &funcs) < 0)
    {
        EXCEPTION("functionality()");
    }
    if (funcs & I2C_FUNC_I2C) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_10BIT_ADDR) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_PROTOCOL_MANGLING) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_PEC) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_BLOCK_PROC_CALL) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_QUICK) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_READ_BYTE) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_WRITE_BYTE) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_READ_BYTE_DATA) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_WRITE_BYTE_DATA) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_READ_WORD_DATA) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_WRITE_WORD_DATA) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_PROC_CALL) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_READ_BLOCK_DATA) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_WRITE_BLOCK_DATA) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_READ_I2C_BLOCK) res |= (1<<(shift++));
    if (funcs & I2C_FUNC_SMBUS_WRITE_I2C_BLOCK) res |= (1<<(shift++));
    return res;
}
JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CAdapter_setAddress
  (JNIEnv *env, jobject obj, jint fd, jshort address)
{
    if  (ioctl(fd, I2C_SLAVE, address) < 0)
    {
        EXCEPTIONV("setAddress(0x%x)", address);
    }
}
JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CAdapter_setPEC
  (JNIEnv *env, jobject obj, jint fd, jboolean pec)
{
    if  (ioctl(fd, I2C_PEC, pec) < 0)
    {
        EXCEPTIONV("setPEC(0x%x)", pec);
    }
}

JNIEXPORT void JNICALL Java_org_vesalainen_dev_i2c_I2CAdapter_set10Bit
  (JNIEnv *env, jobject obj, jint fd, jboolean ten)
{
    if  (ioctl(fd, I2C_TENBIT, ten) < 0)
    {
        EXCEPTIONV("set10Bit(0x%x)", ten);
    }
}
