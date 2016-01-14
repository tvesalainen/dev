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
import org.vesalainen.dev.i2c.mcp342X.MCP342XChannel;

/**
 *
 * @author tkv
 */
public class VoltageDividerChannel implements MCP342XChannel
{
    private final MCP342XChannel channel;
    private final double rel;
    /**
     * Creates a VoltageDividerChannel
     * @param channel
     * @param r1 Resistor in ohms to measured voltage
     * @param r2 Resistor in ohms to ground
     */
    public VoltageDividerChannel(MCP342XChannel channel, double r1, double r2)
    {
        this.channel = channel;
        this.rel = (r1+r2)/r2;
    }

    @Override
    public double measure() throws IOException
    {
        return rel*channel.measure();
    }
    
}
