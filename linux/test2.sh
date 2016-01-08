#! /bin/sh

JAVA=$JAVA_HOME/bin/java
CLASSPATH=../target/classes

sudo $JAVA -cp $CLASSPATH org.vesalainen.dev.i2c.test.Test1

