
static int debug;


#define ERRORRETURNV if (debug) fprintf(stderr, "Error at %d\n", __LINE__);return;
#define ERRORRETURN if (debug) fprintf(stderr, "Error at %d\n", __LINE__);return 0;
#define DEBUG(s) if (debug) fprintf(stderr, "%s at %d\n", (s), __LINE__);fflush(stderr);

#define CHECK(p)	if (!(p)) {ERRORRETURN;}
#define CHECKV(p)	if (!(p)) {ERRORRETURNV;}
#define CHECKEXC if ((*env)->ExceptionCheck(env)) {ERRORRETURN;};
#define CHECKEXCV if ((*env)->ExceptionCheck(env)) {ERRORRETURNV;};

#define EXCEPTION(m) exception(env, "java/io/IOException", m);ERRORRETURN;
#define EXCEPTIONV(m) exception(env, "java/io/IOException", m);ERRORRETURNV;

