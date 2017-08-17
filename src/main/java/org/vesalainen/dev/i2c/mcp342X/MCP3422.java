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
import org.vesalainen.dev.i2c.I2CSMBus;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class MCP3422 extends MCP342X
{

    public MCP3422(I2CSMBus bus) throws IOException
    {
        super(2, bus, (byte)0x68);
    }
    
    public MCP3422(I2CSMBus bus, short slaveAddress) throws IOException
    {
        super(2, bus, slaveAddress);
    }
    
}
