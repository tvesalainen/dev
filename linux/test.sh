#! /bin/sh

JAVA=$JAVA_HOME/bin/java
CLASSPATH=../target/classes

cat /dev/null test1.bin
cat /dev/null test2.bin
sudo $JAVA -cp $CLASSPATH org.vesalainen.dev.test.Test

