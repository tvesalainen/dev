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
import org.vesalainen.dev.i2c.mcp342X.MCP342XChannel;
import org.vesalainen.dev.i2c.mcp342X.MCP342XStandardChannel;

/**
 *
 * @author tkv
 */
public class ADCPiV2
{
    private final MCP3424 mcp1;
    private final MCP3424 mcp2;

    private ADCPiV2(MCP3424 mcp1, MCP3424 mcp2)
    {
        this.mcp1 = mcp1;
        this.mcp2 = mcp2;
    }
    
    public static ADCPiV2 open(int adapter, short slave1, short slave2) throws IOException
    {
        I2CSMBus bus = I2CSMBus.open(adapter);
        return new ADCPiV2(new MCP3424(bus, slave1), new MCP3424(bus, slave2));
    }
    
    public MCP342XChannel getChannel(int channel, MCP342X.Resolution resolution, MCP342X.Gain gain)
    {
        return getChannel(channel, resolution, gain, 0);
    }
    public MCP342XChannel getChannel(int channel, MCP342X.Resolution resolution, MCP342X.Gain gain, double resistor)
    {
        MCP342XChannel ch;
        if (channel <= 4)
        {
            ch = mcp1.getChannel(channel, resolution, gain);
        }
        else
        {
            ch = mcp2.getChannel(channel, resolution, gain);
        }
        return new VoltageDividerChannel(ch, 10000.0+resistor, 6800.0);
    }
    public MCP342XChannel getOptimizingChannel(int channel, Resolution resolution, Gain gain)
    {
        return getOptimizingChannel(channel, resolution, gain, 0);
    }
    public MCP342XChannel getOptimizingChannel(int channel, Resolution resolution, Gain gain, double resistor)
    {
        MCP342XChannel ch;
        if (channel <= 4)
        {
            ch = mcp1.getOptimizingChannel(channel, resolution, gain);
        }
        else
        {
            ch = mcp2.getOptimizingChannel(channel, resolution, gain);
        }
        return new VoltageDividerChannel(ch, 10000.0+resistor, 6800.0);
    }
}
