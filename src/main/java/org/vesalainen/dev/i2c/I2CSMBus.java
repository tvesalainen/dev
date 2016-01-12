/*
 * Copyright (C) 2016 tkv
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.vesalainen.dev.i2c;

import java.io.IOException;
import org.vesalainen.util.EnumSetFlagger;

/**
 *
 * @author tkv
 */
public class I2CSMBus extends I2CAdapter
{

    protected I2CSMBus(int fd)
    {
        super(fd);
    }
    /**
     * Creates I2CSMBus for /dev/i2c-<adapter>
     * <p>examine /sys/class/i2c-dev/ or use i2cdetect -l to find adapter number
     * @param adapter
     * @return
     * @throws IOException 
     * @see <a href="http://git.kernel.org/cgit/linux/kernel/git/torvalds/linux.git/tree/Documentation/i2c/dev-interface">I2C dev</a>
     */
    public static I2CSMBus open(int adapter) throws IOException
    {
        int fd = I2CAdapter.openAdapter(adapter);
        I2CSMBus bus = new I2CSMBus(fd);
        long functionality = bus.functionality(fd);
        bus.funcs = EnumSetFlagger.getSet(I2CFunctionality.class, functionality);
        return bus;
    }
    
    public I2CSlave createSlave(short slaveAddress) throws IOException
    {
        I2CSlave slave = new I2CSlave(fd, slaveAddress);
        long functionality = slave.functionality(fd);
        slave.funcs = EnumSetFlagger.getSet(I2CFunctionality.class, functionality);
        return slave;
    }
    /**
     * This sends a single bit to the device
     * @param bit
     * @throws IOException 
     */
    public void writeQuick(boolean bit) throws IOException
    {
        check(I2CFunctionality.SMBusQuick);
        writeQuick(fd, bit);
    }
    private native void writeQuick(int fd, boolean bit) throws IOException;
    /**
     * This reads a single byte from a device, without specifying a device
     * register. Some devices are so simple that this interface is enough; for
     * others, it is a shorthand if you want to read the same register as in
     * the previous SMBus command.
     * @return
     * @throws IOException 
     */
    public byte readByte() throws IOException
    {
        check(I2CFunctionality.SMBusReadByte);
        return readByte(fd);
    }
    private native byte readByte(int fd) throws IOException;
    /**
     * This operation is the reverse of Receive Byte: it sends a single byte
     * to a device.  See Receive Byte for more information.
     * @param b
     * @throws IOException 
     */
    public void writeByte(byte b) throws IOException
    {
        check(I2CFunctionality.SMBusWriteByte);
        writeByte(fd, b);
    }
    private native void writeByte(int fd, byte b) throws IOException;
    /**
     * This reads a single byte from a device, from a designated register.
     * @param register
     * @return
     * @throws IOException 
     */
    public byte readByteData(byte register) throws IOException
    {
        check(I2CFunctionality.SMBusReadByteData);
        return readByteData(fd, register);
    }
    private native byte readByteData(int fd, byte command) throws IOException;
    /**
     * This operation is very like Read Byte; again, data is written to a
     * device, to a designated register.
     * @param register
     * @param b
     * @throws IOException 
     */
    public void writeByteData(byte register, byte b) throws IOException
    {
        check(I2CFunctionality.SMBusWriteByteData);
        writeByteData(fd, register, b);
    }
    private native void writeByteData(int fd, byte command, byte b) throws IOException;
    /**
     * This operation is very like Read Byte; again, data is read from a
     * device, from a designated register. But this time, the data is a complete word (16 bits).
     * @param register
     * @return
     * @throws IOException 
     */
    public short readWordData(byte register) throws IOException
    {
        check(I2CFunctionality.SMBusReadWordData);
        return readWordData(fd, register);
    }
    private native short readWordData(int fd, byte command) throws IOException;
    /**
     * This is the opposite of the Read Word operation. 16 bits
     * of data is written to a device, to the designated register.
     * @param register
     * @param b
     * @throws IOException 
     */
    public void writeWordData(byte register, short b) throws IOException
    {
        check(I2CFunctionality.SMBusWriteWordData);
        writeWordData(fd, register, b);
    }
    private native void writeWordData(int fd, byte command, short b) throws IOException;
    /**
     * This command reads a block of up to 32 bytes from a device, from a 
     * designated register. Returns read count.
     * @param register
     * @param buf
     * @return
     * @throws IOException 
     */
    public int readBlockData(byte register, byte[] buf) throws IOException
    {
        return readBlockData(register, buf, 0, buf.length);
    }
    /**
     * This command reads a block of up to 32 bytes from a device, from a 
     * designated register. Returns read count.
     * @param register
     * @param buf
     * @param off
     * @param len
     * @return
     * @throws IOException 
     */
    public int readBlockData(byte register, byte[] buf, int off, int len) throws IOException
    {
        check(I2CFunctionality.SMBusReadBlockData);
        return readBlockData(fd, register, buf, off, len);
    }
    private native int readBlockData(int fd, byte command, byte[] buf, int off, int len) throws IOException;
    /**
     * The opposite of the Block Read command, this writes up to 32 bytes to 
     * a device, to a designated register
     * @param register
     * @param buf
     * @throws IOException 
     */
    public void writeBlockData(byte register, byte[] buf) throws IOException
    {
        writeBlockData(register, buf, 0, buf.length);
    }
    /**
     * The opposite of the Block Read command, this writes up to 32 bytes to 
     * a device, to a designated register
     * @param register
     * @param buf
     * @param off
     * @param len
     * @throws IOException 
     */
    public void writeBlockData(byte register, byte[] buf, int off, int len) throws IOException
    {
        check(I2CFunctionality.SMBusWriteBlockData);
        writeBlockData(fd, register, buf, off, len);
    }
    private native void writeBlockData(int fd, byte command, byte[] buf, int off, int len) throws IOException;
    /**
     * This command selects a device register, sends
     * 16 bits of data to it, and reads 16 bits of data in return.
     * @param register
     * @param value
     * @return
     * @throws IOException 
     */
    public short processCall(byte register, short value) throws IOException
    {
        check(I2CFunctionality.SMBusProcCall);
        return processCall(fd, register, value);
    }
    private native short processCall(int fd, byte register, short value) throws IOException;
}
