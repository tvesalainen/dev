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
import java.util.EnumSet;
import org.vesalainen.dev.FileIO;
import org.vesalainen.util.EnumSetFlagger;

/**
 *
 * @author tkv
 */
public class I2CAdapter extends FileIO implements I2C
{
    protected short lastSlave = -1;
    protected EnumSet<I2CFunctionality> funcs;
    
    protected I2CAdapter(int fd)
    {
        super(fd);
    }
    /**
     * Creates I2CAdapter for /dev/i2c-<adapter>
     * <p>examine /sys/class/i2c-dev/ or use i2cdetect -l to find adapter number
     * @param adapter
     * @return
     * @throws IOException 
     * @see <a href="http://git.kernel.org/cgit/linux/kernel/git/torvalds/linux.git/tree/Documentation/i2c/dev-interface">I2C dev</a>
     */
    public static I2CAdapter open(int adapter) throws IOException
    {
        int fd = I2CAdapter.openAdapter(adapter);
        I2CAdapter i2c = new I2CAdapter(fd);
        long functionality = i2c.functionality(fd);
        i2c.funcs = EnumSetFlagger.getSet(I2CFunctionality.class, functionality);
        return i2c;
    }
    
    static native int openAdapter(int adapter) throws IOException;
    protected native long functionality(int fd) throws IOException;
    /**
     * Set device lastSlave for following methods
     * @param address
     * @throws IOException 
     */
    @Override
    public void setAddress(short address) throws IOException
    {
        if (this.lastSlave != address)
        {
            this.lastSlave = address;
            setAddress(fd, address);
        }
    }
    private native void setAddress(int fd, short address) throws IOException;

    @Override
    public EnumSet<I2CFunctionality> getFunctionality()
    {
        return funcs;
    }
    /**
     * Set packet error checking
     * @param pec
     * @throws IOException 
     */
    @Override
    public void setPEC(boolean pec) throws IOException
    {
        check(I2CFunctionality.SMBusPEC);
        setPEC(fd, pec);
    }
    private native void setPEC(int fd, boolean pec) throws IOException;
    /**
     * Set 10 bit addressing
     * @param tenBit
     * @throws IOException 
     */
    @Override
    public void set10Bit(boolean tenBit) throws IOException
    {
        check(I2CFunctionality.TenBitAddr);
        set10Bit(fd, tenBit);
    }
    private native void set10Bit(int fd, boolean tenBit) throws IOException;
    
    protected void check(I2CFunctionality f)
    {
        if (!funcs.contains(f))
        {
            throw new UnsupportedOperationException(f+" not supported with current device");
        }
    }
}
