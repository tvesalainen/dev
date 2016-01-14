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

import java.io.IOException;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import static org.vesalainen.dev.i2c.mcp342X.MCP342X.Vref;

/**
 *
 * @author tkv
 */
public class MCP342XOptimizingChannel extends MCP342XStandardChannel
{
    public static final double Limit = Vref/3.0;

    MCP342XOptimizingChannel(MCP342X mcp342x, int channel)
    {
        super(mcp342x, channel, Resolution.Bits18, Gain.X1);
    }

    /**
     * Return measure. Gain is altered to get measurement  greater than Vref/3.
     * @return
     * @throws IOException 
     */
    @Override
    public double measure() throws IOException
    {
        double voltage = mcp342x.rawMeasure(channel, resolution, gain);
        if (Double.isInfinite(voltage))
        {
            if (decGain())
            {
                return measure();
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
                return measure();
            }
        }
        return voltage/mcp342x.getPGA();
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
            return true;
        }
    }
}
