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
package org.vesalainen.dev.i2c.mcp342X;

import org.vesalainen.dev.VoltageSource;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static final double Vref = 2.048;
    public static final double[] PGA = new double[] {1.0, 2.0, 4.0, 8.0};
    public static final double[] LSB = new double[4];
    public static final int[] MaxCode = new int[4];
    public static final int[] MinCode = new int[4];
    public static final long[] Delay = new long[] {(long)Math.ceil(1000.0/240.0), (long)Math.ceil(1000.0/60.0), (long)Math.ceil(1000.0/15.0), (long)Math.ceil(1000.0/3.75)};
    protected I2CSMBus bus;
    protected I2CSlave slave;
    protected byte config;
    protected int channelCount = 2;
    protected ReentrantLock lock = new ReentrantLock();
    protected byte[] buf = new byte[32];

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
    MCP342X()
    {
        this.channelCount = 4;
    }

    public MCP342X(int channelCount, I2CSMBus bus, short slaveAddress) throws IOException
    {
        checkSlaveAddress(slaveAddress);
        this.channelCount = channelCount;
        this.bus = bus;
        this.slave = bus.createSlave(slaveAddress);
    }
    /**
     * Creates channel
     * @param channel
     * @param resolution
     * @param gain
     * @return 
     */
    public VoltageSource getChannel(int channel, Resolution resolution, Gain gain)
    {
        checkChannel(channel);
        return new MCP342XStandardChannel(this, channel, resolution, gain);
    }
    /**
     * Creates optimized channel. Optimized channel tries to get measurement
     * @param channel
     * @return 
     */
    public VoltageSource getOptimizingChannel(int channel)
    {
        checkChannel(channel);
        return new MCP342XOptimizingChannel(this, channel);
    }
    /**
     * Returns -2.048 - 2.048 or positive/negative infinity
     * @param channel
     * @param resolution
     * @param gain
     * @return
     * @throws IOException 
     */
    public double measure(int channel, Resolution resolution, Gain gain) throws IOException
    {
        double rm = rawMeasure(channel, resolution, gain)/PGA[get2Bit(0)];
        System.err.println(getGain());
        return rm;
    }
    double rawMeasure(int channel, Resolution resolution, Gain gain) throws IOException
    {
        checkChannel(channel);
        lock.lock();
        try
        {
            setContinousConversion(false);
            setReady(true);
            setChannel(channel);
            setResolution(resolution);
            setGain(gain);
            long n1 = System.nanoTime();
            slave.writeByte(config);
            Thread.sleep(Delay[resolution.ordinal()]);
            int len = slave.read(buf);
            int cnt = 0;
            while (isSet(buf[len-1], 7))
            {
                len = slave.read(buf);
                cnt++;
            }
            long n2 = System.nanoTime();
            long dn = n2-n1;
        }
        catch (InterruptedException ex)
        {
            throw new IOException(ex);
        }        
        finally
        {
            lock.unlock();
        }
        return getRawVoltage(buf);
    }
    double getVoltage(byte... buf)
    {
        return getRawVoltage(buf)/PGA[get2Bit(0)];
    }
    double getRawVoltage(byte... buf)
    {
        double sign=1;
        int value = 0;
        int res = get2Bit(2);
        switch (res)
        {
            case 0: // 12
                if (isSet(buf[0], 3))
                {
                    sign = -1;
                }
                value = ((0b111 & (buf[0] & 0xff))<<8)+(buf[1] & 0xff);
                break;
            case 1: // 14
                if (isSet(buf[0], 5))
                {
                    sign = -1;
                }
                value = ((0b11111 & (buf[0] & 0xff))<<8)+(buf[1] & 0xff);
                break;
            case 2: // 16
                if (isSet(buf[0], 7))
                {
                    sign = -1;
                }
                value = ((0b1111111 & (buf[0] & 0xff))<<8)+(buf[1] & 0xff);
                break;
            case 3: // 18
                if (isSet(buf[0], 1))
                {
                    sign = -1;
                }
                value = ((0b1 & (buf[0] & 0xff))<<16)+((buf[1] & 0xff)<<8)+(buf[2] & 0xff);
                break;
        }
        if (value == MaxCode[res])
        {
            if (sign > 0)
            {
                return Double.POSITIVE_INFINITY;
            }
            else
            {
                return Double.NEGATIVE_INFINITY;
            }
        }
        return sign*value*LSB[res];
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
        checkChannel(ch);
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
    
    public double getLSB()
    {
        return LSB[get2Bit(2)]; 
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

    private void checkChannel(int channel)
    {
        if (channel < 1 || channel > channelCount)
        {
            throw new IllegalArgumentException("channel not in range 1 - "+channelCount);
        }
    }
    private void checkSlaveAddress(short slaveAddress)
    {
        if ((slaveAddress>>3) != 0b1101)
        {
            throw new IllegalArgumentException("slave address not correct "+slaveAddress);
        }
    }
}
