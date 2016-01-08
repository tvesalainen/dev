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
        EXCEPTION(filename);
    }
    return fd;
}
