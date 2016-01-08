MACHINE=$(shell uname -m)
JAVAH=$(JAVA_HOME)/bin/javah
PROJECTDIR=..
JAVASRC=$(PROJECTDIR)/src/main/java
RESOURCES=$(PROJECTDIR)/src/main/resources
CFLAGS= -pthread -fPIC -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux
SOURCEDIR=$(PROJECTDIR)/linux
CLASSPATH=$(PROJECTDIR)/target/classes
CLASSFILES=$(CLASSPATH)/org/vesalainen/dev/FileIO.class
CLASSES=org.vesalainen.dev.FileIO

$(HEADER):	$(CLASSFILES)
	$(JAVAH) -v -d $(SOURCEDIR) -cp $(CLASSPATH) $(CLASSES)

FileIO.o:	FileIO.c org_vesalainen_dev_FileIO.h

Dev.so:	FileIO.o Exception.o
	ld -shared -o Dev.so FileIO.o Exception.o -lc

install:	Dev.so
	cp Dev.so $(RESOURCES)/libDev$(MACHINE).so

all:	install

clean:
	rm *.o
	rm *.so

