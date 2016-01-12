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

import java.io.IOException;
import java.util.Arrays;
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

    protected static final double Vref = 2.048;
    protected static final double[] PGA = new double[] {1.0, 2.0, 4.0, 8.0};
    protected static final double[] LSB = new double[4];
    protected static final int[] MaxCode = new int[4];
    protected static final int[] MinCode = new int[4];
    protected I2CSMBus bus;
    protected I2CSlave slave;
    protected byte config;

    static
    {
        for (int ii=0;ii<4;ii++)
        {
            int bits = 12+2*ii;
            LSB[ii] = 2.0*Vref/Math.pow(2, bits);
            MaxCode[ii] = (int) (Math.pow(2, bits-1)-1);
            MinCode[ii] = (int) -Math.pow(2, bits-1);
        }
    }
    public MCP342X()
    {
    }

    public MCP342X(I2CSMBus bus, short slaveAddress) throws IOException
    {
        this.bus = bus;
        this.slave = bus.createSlave(slaveAddress);
    }

    public double measure(int channel, Resolution resolution, Gain gain) throws IOException
    {
        setContinousConversion(false);
        setReady(true);
        setChannel(channel);
        setResolution(resolution);
        setGain(gain);
        System.err.println(config);
        slave.writeByte(config);
        byte[] buf;
        if (resolution == Resolution.Bits18)
        {
            buf = new byte[4];
        }
        else
        {
            buf = new byte[3];
        }
        int len = slave.readBlockData(config, buf);
        System.err.println("len="+len+" "+Arrays.toString(buf));
        return 0;
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
    public double getPGA()
    {
        return PGA[get2Bit(0)]; 
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
