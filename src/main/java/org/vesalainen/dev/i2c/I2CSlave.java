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

/**
 *
 * @author tkv
 */
public class I2CSlave extends I2CSMBus
{
    private final short slave;

    I2CSlave(int fd, short slave)
    {
        super(fd);
        this.slave = slave;
    }

    @Override
    public short processCall(byte register, short value) throws IOException
    {
        setAddress(slave);
        return super.processCall(register, value);
    }

    @Override
    public void writeBlockData(byte register, byte[] buf, int off, int len) throws IOException
    {
        setAddress(slave);
        super.writeBlockData(register, buf, off, len);
    }

    @Override
    public void writeBlockData(byte register, byte[] buf) throws IOException
    {
        setAddress(slave);
        super.writeBlockData(register, buf);
    }

    @Override
    public int readBlockData(byte register, byte[] buf, int off, int len) throws IOException
    {
        setAddress(slave);
        return super.readBlockData(register, buf, off, len);
    }

    @Override
    public int readBlockData(byte register, byte[] buf) throws IOException
    {
        setAddress(slave);
        return super.readBlockData(register, buf);
    }

    @Override
    public void writeWordData(byte register, short b) throws IOException
    {
        setAddress(slave);
        super.writeWordData(register, b);
    }

    @Override
    public short readWordData(byte register) throws IOException
    {
        setAddress(slave);
        return super.readWordData(register);
    }

    @Override
    public void writeByteData(byte register, byte b) throws IOException
    {
        setAddress(slave);
        super.writeByteData(register, b);
    }

    @Override
    public byte readByteData(byte register) throws IOException
    {
        setAddress(slave);
        return super.readByteData(register);
    }

    @Override
    public void writeByte(byte b) throws IOException
    {
        setAddress(slave);
        super.writeByte(b);
    }

    @Override
    public byte readByte() throws IOException
    {
        setAddress(slave);
        return super.readByte();
    }

    @Override
    public void writeQuick(boolean bit) throws IOException
    {
        setAddress(slave);
        super.writeQuick(bit);
    }
    
    
}
