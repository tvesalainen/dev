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
package org.vesalainen.dev.i2c.adcpi;

import org.vesalainen.dev.VoltageSource;
import static org.vesalainen.dev.i2c.mcp342X.MCP342X.Vref;
import org.vesalainen.math.AbstractLine;
import org.vesalainen.math.Line;
import org.vesalainen.util.logging.JavaLogging;

/**
 * LineCorrectedChannel scales measurements by using line. X-axis represents
 * raw measurements and Y-axis wanted output.
 * <p>Line is given by 1 or 2 points. 1 point version goes through (0, 0) 
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class LineCorrectedChannel extends JavaLogging implements VoltageSource
{
    private final VoltageSource channel;
    private final Line line;

    public LineCorrectedChannel(VoltageSource channel, double slope)
    {
        super(LineCorrectedChannel.class);
        this.channel = channel;
        this.line = new AbstractLine(slope, 0, 0);
    }
    
    /**
     * Creates a LineCorrectedChannel
     * @param channel
     * @param points slope or x, y or x1, y1, x2, y2
     */
    public LineCorrectedChannel(VoltageSource channel, double... points)
    {
        super(LineCorrectedChannel.class);
        this.channel = channel;
        switch (points.length)
        {
            case 1:
                this.line = new AbstractLine(points[0], 0, 0);
                break;
            case 2:
                this.line = new AbstractLine(0, 0, points[0], points[1]);
                break;
            case 4:
                this.line = new AbstractLine(points[0], points[1], points[2], points[3]);
                break;
            default:
                throw new IllegalArgumentException("number of points must be 1/2/4");
        }
    }

    @Override
    public double getAsDouble()
    {
        double measure = channel.getAsDouble();
        double corrected = line.getY(measure);
        finer("corrected %f -> %f", measure, corrected);
        return corrected;
    }

    public double getSlope()
    {
        return line.getSlope();
    }

    @Override
    public double min()
    {
        return 0;
    }

    @Override
    public double max()
    {
        return line.getY(Vref);
    }
    
    @Override
    public String toString()
    {
        return "LineCorrectedChannel{" + "channel=" + channel + '}';
    }
    
}
