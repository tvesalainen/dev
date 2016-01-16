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
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author tkv
 */
public class I2CSlave extends I2CSMBus
{
    private final I2CSMBus bus;
    private final short slave;
    private final ReentrantLock lock = new ReentrantLock();

    I2CSlave(I2CSMBus bus, short slave)
    {
        super(bus.getFd());
        this.bus = bus;
        this.slave = slave;
    }

    @Override
    public short processCall(byte register, short value) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.processCall(register, value);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void writeBlockData(byte register, byte[] buf, int off, int len) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.writeBlockData(register, buf, off, len);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void writeBlockData(byte register, byte[] buf) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.writeBlockData(register, buf);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public int readBlockData(byte register, byte[] buf, int off, int len) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.readBlockData(register, buf, off, len);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public int readBlockData(byte register, byte[] buf) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.readBlockData(register, buf);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void writeWordData(byte register, short b) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.writeWordData(register, b);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public short readWordData(byte register) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.readWordData(register);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void writeByteData(byte register, byte b) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.writeByteData(register, b);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public byte readByteData(byte register) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.readByteData(register);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void writeByte(byte b) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.writeByte(b);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public byte readByte() throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.readByte();
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void writeQuick(boolean bit) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.writeQuick(bit);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.write(buf, off, len);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void write(byte... bytes) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.write(bytes);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public int read(byte[] buf, int off, int len) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.read(buf, off, len);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public int read(byte[] buf) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.read(buf);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public void write(byte b) throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            super.write(b);
        }
        finally
        {
            lock.unlock();
        }
    }

    @Override
    public byte read() throws IOException
    {
        lock.lock();
        try
        {
            bus.setAddress(slave);
            return super.read();
        }
        finally
        {
            lock.unlock();
        }
    }

}
