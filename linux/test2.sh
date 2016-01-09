#! /bin/sh

JAVA=$JAVA_HOME/bin/java
CLASSPATH=../target/classes:../../util/target/util-1.0.5.jar

sudo $JAVA -cp $CLASSPATH org.vesalainen.dev.i2c.test.Test1;

