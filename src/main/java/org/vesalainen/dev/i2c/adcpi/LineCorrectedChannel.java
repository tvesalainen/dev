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
import org.vesalainen.dev.VoltageSource;
import org.vesalainen.math.AbstractLine;
import org.vesalainen.math.Line;

/**
 * LineCorrectedChannel scales measurements by using line. X-axis represents
 * raw measurements and Y-axis wanted output.
 * <p>Line is given by 1 or 2 points. 1 point version goes through (0, 0) 
 * @author tkv
 */
public class LineCorrectedChannel implements VoltageSource
{
    protected final VoltageSource channel;
    protected final Line line;

    public LineCorrectedChannel(VoltageSource channel, double slope)
    {
        this.channel = channel;
        this.line = new AbstractLine(slope, 0, 0);
    }
    
    /**
     * Creates a LineCorrectedChannel
     * @param channel
     * @param points x, y or x1, y1, x2, y2
     */
    public LineCorrectedChannel(VoltageSource channel, double... points)
    {
        this.channel = channel;
        switch (points.length)
        {
            case 2:
                this.line = new AbstractLine(0, 0, points[0], points[1]);
                break;
            case 4:
                this.line = new AbstractLine(points[0], points[1], points[2], points[3]);
                break;
            default:
                throw new IllegalArgumentException("number of points must be 2/4");
        }
    }

    @Override
    public double getAsDouble()
    {
        double measure = channel.getAsDouble();
        return line.getY(measure);
    }
    
}
