/*
 * Copyright (C) 2016 Timo Vesalainen <timo.vesalainen@iki.fi>
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
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public interface SMBus extends I2C
{

    /**
     * This command selects a device register, sends
     * 16 bits of data to it, and reads 16 bits of data in return.
     * @param register
     * @param value
     * @return
     * @throws IOException
     */
    short processCall(byte register, short value) throws IOException;

    /**
     * This command reads a block of up to 32 bytes from a device, from a
     * designated register. Returns read count.
     * @param register
     * @param buf
     * @return
     * @throws IOException
     */
    int readBlockData(byte register, byte[] buf) throws IOException;

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
    int readBlockData(byte register, byte[] buf, int off, int len) throws IOException;

    /**
     * This reads a single byte from a device, without specifying a device
     * register. Some devices are so simple that this interface is enough; for
     * others, it is a shorthand if you want to read the same register as in
     * the previous SMBus command.
     * @return
     * @throws IOException
     */
    byte readByte() throws IOException;

    /**
     * This reads a single byte from a device, from a designated register.
     * @param register
     * @return
     * @throws IOException
     */
    byte readByteData(byte register) throws IOException;

    /**
     * This operation is very like Read Byte; again, data is read from a
     * device, from a designated register. But this time, the data is a complete word (16 bits).
     * @param register
     * @return
     * @throws IOException
     */
    short readWordData(byte register) throws IOException;

    /**
     * The opposite of the Block Read command, this writes up to 32 bytes to
     * a device, to a designated register
     * @param register
     * @param buf
     * @throws IOException
     */
    void writeBlockData(byte register, byte[] buf) throws IOException;

    /**
     * The opposite of the Block Read command, this writes up to 32 bytes to
     * a device, to a designated register
     * @param register
     * @param buf
     * @param off
     * @param len
     * @throws IOException
     */
    void writeBlockData(byte register, byte[] buf, int off, int len) throws IOException;

    /**
     * This operation is the reverse of Receive Byte: it sends a single byte
     * to a device.  See Receive Byte for more information.
     * @param b
     * @throws IOException
     */
    void writeByte(byte b) throws IOException;

    /**
     * This operation is very like Read Byte; again, data is written to a
     * device, to a designated register.
     * @param register
     * @param b
     * @throws IOException
     */
    void writeByteData(byte register, byte b) throws IOException;

    /**
     * This sends a single bit to the device
     * @param bit
     * @throws IOException
     */
    void writeQuick(boolean bit) throws IOException;

    /**
     * This is the opposite of the Read Word operation. 16 bits
     * of data is written to a device, to the designated register.
     * @param register
     * @param b
     * @throws IOException
     */
    void writeWordData(byte register, short b) throws IOException;
    
}
