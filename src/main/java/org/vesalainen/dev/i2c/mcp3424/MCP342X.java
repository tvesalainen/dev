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
package org.vesalainen.dev.i2c.mcp3424;

import org.vesalainen.dev.i2c.I2CSMBus;
import org.vesalainen.dev.i2c.I2CSlave;

/**
 *
 * @author tkv
 */
public class MCP342X
{
    public enum SampleRate {SPS240, SPS60, SPS15, SPS3_75};
    public enum Resolution {Bits12, Bits14, Bits16, Bits18};
    public enum Gain {X1, X2, X4, X8}
    protected I2CSMBus bus;
    protected I2CSlave slave;
    protected byte config;

    public MCP342X()
    {
    }

    public MCP342X(I2CSMBus bus, short slaveAddress)
    {
        this.bus = bus;
        this.slave = bus.createSlave(slaveAddress);
    }

    public byte getConfig()
    {
        return config;
    }
    
    public void setReady(boolean ready)
    {
        set(7, ready);
    }
    public boolean isReady()
    {
        return isSet(7);
    }
    protected void setChannel(int ch)
    {
        set(6, ch == 3 || ch == 4);
        set(5, ch == 2 || ch == 4);
    }
    protected int getChannel()
    {
        return get2Bit(5)+1;
    }
    private int get2Bit(int bit)
    {
        int res = 0;
        if (isSet(bit+1))
        {
            res = 2;
        }
        if (isSet(bit))
        {
            res++;
        }
        return res;
    }
    public void setContinousConversion(boolean continuous)
    {
        set(4, continuous);
    }
    public boolean getContinousConversion()
    {
        return isSet(4);
    }
    public void setSampleRate(SampleRate rate)
    {
        set(3, rate == SampleRate.SPS15 || rate == SampleRate.SPS3_75);
        set(2, rate == SampleRate.SPS60 || rate == SampleRate.SPS3_75);
    }
    public SampleRate getSampleRate()
    {
        return SampleRate.values()[get2Bit(2)];
    }
    public void setResolution(Resolution res)
    {
        set(3, res == Resolution.Bits16 || res == Resolution.Bits18);
        set(2, res == Resolution.Bits14 || res == Resolution.Bits18);
    }
    public Resolution getResolution()
    {
        return Resolution.values()[get2Bit(2)];
    }
    public void setGain(Gain g)
    {
        set(1, g == Gain.X4 || g == Gain.X8);
        set(0, g == Gain.X2 || g == Gain.X8);
    }
    public Gain getGain()
    {
        return Gain.values()[get2Bit(0)];
    }
    public int getPGA()
    {
        switch (getGain())
        {
            case X1:
                return 1;
            case X2:
                return 2;
            case X4:
                return 4;
            case X8:
                return 8;
        }
        throw new IllegalArgumentException();
    }
    
    protected void set(int bit, boolean value)
    {
        config = set(config, bit, value);
    }
    protected boolean isSet(int bit)
    {
        return isSet(config, bit);
    }
    protected static byte set(byte b, int bit, boolean value)
    {
        if (value)
        {
            b |= (1 << bit);
        }
        else
        {
            b &= ~(1 << bit);
        }
        return b;
    }

    protected static boolean isSet(byte b, int bit)
    {
        return (b & (1 << bit)) != 0;
    }

}
