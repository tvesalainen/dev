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
package org.vesalainen.dev.i2c.adcpi;

import java.io.IOException;
import org.vesalainen.dev.i2c.I2CSMBus;
import org.vesalainen.dev.i2c.mcp342X.MCP3424;
import org.vesalainen.dev.i2c.mcp342X.MCP342X;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import org.vesalainen.dev.VoltageSource;

/**
 *
 * @author tkv
 */
public class ADCPiV2 implements AutoCloseable
{
    private I2CSMBus smBus;
    private MCP3424 mcp1;
    private MCP3424 mcp2;

    public ADCPiV2(I2CSMBus smBus, MCP3424 mcp1, MCP3424 mcp2)
    {
        this.smBus = smBus;
        this.mcp1 = mcp1;
        this.mcp2 = mcp2;
    }

    public static ADCPiV2 open(int adapter, short slave1, short slave2) throws IOException
    {
        I2CSMBus bus = I2CSMBus.open(adapter);
        return new ADCPiV2(bus, new MCP3424(bus, slave1), new MCP3424(bus, slave2));
    }
    
    public VoltageSource getChannel(int channel, MCP342X.Resolution resolution, MCP342X.Gain gain)
    {
        return getVoltageDividerChannel(channel, resolution, gain, 0);
    }
    public VoltageSource getVoltageDividerChannel(int channel, Resolution resolution, Gain gain, double resistor)
    {
        return new VoltageDividerChannel(getStdChannel(channel, resolution, gain), 10000.0+resistor, 6800.0);
    }
    public VoltageSource getLineCorrectedChannel(int channel, Resolution resolution, Gain gain, double... points)
    {
        return new LineCorrectedChannel(getStdChannel(channel, resolution, gain), points);
    }
    private VoltageSource getStdChannel(int channel, Resolution resolution, Gain gain)
    {
        if (channel <= 4)
        {
            return mcp1.getChannel(channel, resolution, gain);
        }
        else
        {
            return mcp2.getChannel(channel-4, resolution, gain);
        }
    }
    public VoltageSource getOptimizingChannel(int channel)
    {
        return getOptimizingVoltageDividerChannel(channel, 0);
    }
    public VoltageSource getOptimizingVoltageDividerChannel(int channel, double resistor)
    {
        return new VoltageDividerChannel(getOptChannel(channel), 10000.0+resistor, 6800.0);
    }

    public VoltageSource getOptimizingLineCorrectedChannel(int channel, double... points)
    {
        return new LineCorrectedChannel(getOptChannel(channel), points);
    }
    private VoltageSource getOptChannel(int channel)
    {
        VoltageSource ch;
        if (channel <= 4)
        {
            return mcp1.getOptimizingChannel(channel);
        }
        else
        {
            return mcp2.getOptimizingChannel(channel-4);
        }
    }
    @Override
    public void close() throws IOException
    {
        if (smBus != null)
        {
            smBus.close();
            smBus = null;
            mcp1 = null;
            mcp2 = null;
        }
    }
}
