MACHINE=$(shell uname -m)
JAVAH=$(JAVA_HOME)/bin/javah
PROJECTDIR=..
JAVASRC=$(PROJECTDIR)/src/main/java
RESOURCES=$(PROJECTDIR)/src/main/resources
CFLAGS= -pthread -fPIC -I $(JAVA_HOME)/include -I $(JAVA_HOME)/include/linux
SOURCEDIR=$(PROJECTDIR)/linux
CLASSPATH=$(PROJECTDIR)/target/classes
CLASSFILES=$(CLASSPATH)/org/vesalainen/dev/FileIO.class $(CLASSPATH)/org/vesalainen/dev/i2c/I2CAdapter.class $(CLASSPATH)/org/vesalainen/dev/i2c/I2CSMBus.class
CLASSES=org.vesalainen.dev.FileIO org.vesalainen.dev.i2c.I2CAdapter org.vesalainen.dev.i2c.I2CSMBus

headers:	$(CLASSFILES)
	$(JAVAH) -v -d $(SOURCEDIR) -cp $(CLASSPATH) $(CLASSES)

FileIO.o:   FileIO.c org_vesalainen_dev_FileIO.h

I2CAdapter.o:   I2CAdapter.c org_vesalainen_dev_i2c_I2CAdapter.h

I2CSMBus.o:   I2CSMBus.c org_vesalainen_dev_i2c_I2CSMBus.h

Dev.so:	I2CSMBus.o I2CAdapter.o FileIO.o Exception.o
	ld -shared -o Dev.so I2CSMBus.o I2CAdapter.o FileIO.o Exception.o -lc

install:	Dev.so
	cp Dev.so $(RESOURCES)/libDev$(MACHINE).so

all:	headers install

clean:
	rm *.o
	rm *.so

