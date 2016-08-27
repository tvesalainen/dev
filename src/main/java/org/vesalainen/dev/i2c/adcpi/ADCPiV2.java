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
import static java.util.logging.Level.*;
import org.vesalainen.dev.i2c.I2CSMBus;
import org.vesalainen.dev.i2c.mcp342X.MCP3424;
import org.vesalainen.dev.i2c.mcp342X.MCP342X;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import org.vesalainen.dev.VoltageSource;
import org.vesalainen.util.logging.JavaLogging;

/**
 *
 * @author tkv
 */
public class ADCPiV2 extends JavaLogging implements AutoCloseable
{
    private I2CSMBus smBus;
    private MCP3424 mcp1;
    private MCP3424 mcp2;

    public ADCPiV2(I2CSMBus smBus, MCP3424 mcp1, MCP3424 mcp2)
    {
        super(ADCPiV2.class);
        this.smBus = smBus;
        this.mcp1 = mcp1;
        this.mcp2 = mcp2;
    }

    public static ADCPiV2 open(int adapter, short slave1, short slave2) throws IOException
    {
        JavaLogging logger = JavaLogging.getLogger(ADCPiV2.class);
        logger.config("ADCPiV2 open(%d, 0x%x, 0x%x)", adapter,  slave1, slave2);
        I2CSMBus bus = I2CSMBus.open(adapter);
        ADCPiV2 adc = new ADCPiV2(bus, new MCP3424(bus, slave1), new MCP3424(bus, slave2));
        if (logger.isLoggable(CONFIG))
        {
            for (int ii=1;ii<=8;ii++)
            {
                VoltageSource oc = adc.getOptimizingChannel(ii, Resolution.Bits12);
                logger.config("initial voltage on channel %d = %fV", ii, oc.getAsDouble());
            }
        }
        return adc;
    }
    
    public VoltageSource getChannel(int channel, MCP342X.Resolution resolution, MCP342X.Gain gain)
    {
        return getStdChannel(channel, resolution, gain);
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
    public VoltageSource getOptimizingChannel(int channel, Resolution resolution)
    {
        return getOptChannel(channel, resolution);
    }
    public VoltageSource getOptimizingVoltageDividerChannel(int channel, Resolution resolution, double resistor)
    {
        return new VoltageDividerChannel(getOptChannel(channel, resolution), 10000.0+resistor, 6800.0);
    }

    public VoltageSource getOptimizingLineCorrectedChannel(int channel, Resolution resolution, double... points)
    {
        return new LineCorrectedChannel(getOptChannel(channel, resolution), points);
    }
    private VoltageSource getOptChannel(int channel, Resolution resolution)
    {
        if (channel <= 4)
        {
            return mcp1.getOptimizingChannel(channel, resolution);
        }
        else
        {
            return mcp2.getOptimizingChannel(channel-4, resolution);
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

    @Override
    public String toString()
    {
        return "ADCPiV2";
    }
}
