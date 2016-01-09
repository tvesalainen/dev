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

static int debug;


#define ERRORRETURNV if (debug) fprintf(stderr, "Error at %d\n", __LINE__);return;
#define ERRORRETURN if (debug) fprintf(stderr, "Error at %d\n", __LINE__);return 0;
#define DEBUG(s) if (debug) fprintf(stderr, "%s at %d\n", (s), __LINE__);fflush(stderr);

#define CHECK(p)	if (!(p)) {ERRORRETURN;}
#define CHECKV(p)	if (!(p)) {ERRORRETURNV;}
#define CHECKEXC if ((*env)->ExceptionCheck(env)) {ERRORRETURN;};
#define CHECKEXCV if ((*env)->ExceptionCheck(env)) {ERRORRETURNV;};

#define EXCEPTION(...) exception(env, "java/io/IOException", __VA_ARGS__);ERRORRETURN;
#define EXCEPTIONV(...) exception(env, "java/io/IOException", __VA_ARGS__);ERRORRETURNV;

