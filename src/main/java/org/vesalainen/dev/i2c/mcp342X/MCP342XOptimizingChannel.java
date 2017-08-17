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
package org.vesalainen.dev.i2c.mcp342X;

import java.io.IOException;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import static org.vesalainen.dev.i2c.mcp342X.MCP342X.Vref;
import org.vesalainen.util.logging.JavaLogging;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class MCP342XOptimizingChannel extends MCP342XStandardChannel
{
    public static final double Limit = Vref/3.0;
    private JavaLogging log;

    MCP342XOptimizingChannel(MCP342X mcp342x, int channel, Resolution resolution)
    {
        super(mcp342x, channel, resolution, Gain.X1);
        log = new JavaLogging(MCP342XOptimizingChannel.class);
    }

    /**
     * Return voltage. Gain is altered to get measurement  greater than Vref/3.
     * @return 
     */
    
    @Override
    public double getAsDouble()
    {
        try
        {
            double voltage = mcp342x.rawMeasure(channel, resolution, gain);
            log.finer("rawMeasure(%d, %s, %s) = %f", channel, resolution, gain, voltage);
            if (Double.isInfinite(voltage))
            {
                if (decGain())
                {
                    return getAsDouble();
                }
                else
                {
                    return voltage; // -/+ infinity
                }
            }
            if (Math.abs(voltage) < Limit)
            {
                if (incGain())
                {
                    return getAsDouble();
                }
            }
            double mea = voltage/mcp342x.getPGA();
            log.finer("measured %f with %f", mea, mcp342x.getPGA());
            return mea;
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }
    
    private boolean incGain()
    {
        if (gain == Gain.X8)
        {
            return false;
        }
        else
        {
            gain = Gain.values()[gain.ordinal()+1];
            log.finest("gain -> %s", gain);
            return true;
        }
    }
    private boolean decGain()
    {
        if (gain == Gain.X1)
        {
            return false;
        }
        else
        {
            gain = Gain.values()[gain.ordinal()-1];
            log.finest("gain -> %s", gain);
            return true;
        }
    }

    @Override
    public String toString()
    {
        return "MCP342XOptimizingChannel{" + "mcp342x=" + mcp342x + ", channel=" + channel + ", resolution=" + resolution + ", gain=" + gain + '}';
    }
}
