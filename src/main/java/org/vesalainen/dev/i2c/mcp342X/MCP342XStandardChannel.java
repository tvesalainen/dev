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

import org.vesalainen.dev.VoltageSource;
import java.io.IOException;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import static org.vesalainen.dev.i2c.mcp342X.MCP342X.Vref;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class MCP342XStandardChannel implements VoltageSource
{
    protected final MCP342X mcp342x;
    protected final int channel;
    protected final Resolution resolution;
    protected Gain gain;

    MCP342XStandardChannel(MCP342X mcp342x, int channel, Resolution resolution, Gain gain)
    {
        this.mcp342x = mcp342x;
        this.channel = channel;
        if (resolution == null)
        {
            throw new NullPointerException("resolution");
        }
        this.resolution = resolution;
        if (gain == null)
        {
            throw new NullPointerException("gain");
        }
        this.gain = gain;
    }

    /**
     * Return voltage. 
     * @return 
     */
    @Override
    public double getAsDouble()
    {
        try
        {
            return mcp342x.measure(channel, resolution, gain);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public double min()
    {
        return 0;
    }

    @Override
    public double max()
    {
        return Vref;
    }

    @Override
    public String toString()
    {
        return "MCP342XStandardChannel{" + "mcp342x=" + mcp342x + ", channel=" + channel + ", resolution=" + resolution + ", gain=" + gain + '}';
    }
    
}
