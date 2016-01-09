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
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>

#include <jni.h>


void exception(JNIEnv * env, const char* clazz, const char* fmt, ...)
{
    jclass exc;
    char buf[256];
    char message[200];
    va_list ap;
    int n;
    
    va_start(ap, fmt);
    n = vsnprintf(message, sizeof(message), fmt, ap);
    if (n > sizeof(message) || n < 0)
    {
        fprintf(stderr,"%s truncated\n", fmt);
    }
    va_end(ap);
    
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



