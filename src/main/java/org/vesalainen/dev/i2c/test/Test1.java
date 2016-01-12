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
package org.vesalainen.dev.i2c.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vesalainen.dev.i2c.I2CAdapter;
import org.vesalainen.dev.i2c.I2CSMBus;
import org.vesalainen.dev.i2c.mcp3424.MCP342X;

/**
 *
 * @author tkv
 */
public class Test1
{
    public static void test1()
    {
        try (I2CAdapter i2c = I2CAdapter.open(1))
        {
            i2c.setAddress((short)0x68);
            System.err.println(i2c.getFunctionality());
            i2c.set10Bit(false);
            i2c.setPEC(false);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void test2()
    {
        try (I2CSMBus bus = I2CSMBus.open(1))
        {
            MCP342X mcp = new MCP342X(bus, (short)0x68);
            mcp.measure(1, MCP342X.Resolution.Bits18, MCP342X.Gain.X1);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String... args)
    {
        test1();
        test2();
    }
}
