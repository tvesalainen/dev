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

import org.junit.Test;
import static org.junit.Assert.*;
import org.vesalainen.dev.FixedVoltage;

/**
 *
 * @author tkv
 */
public class VoltageDividerChannelTest
{
    private static double Epsilon = 1e-10;
    
    public VoltageDividerChannelTest()
    {
    }

    @Test
    public void test1()
    {
        VoltageDividerChannel vdc = new VoltageDividerChannel(new FixedVoltage(3), 1000, 1000);
        assertEquals(6, vdc.getAsDouble(), Epsilon);
    }
    
    @Test
    public void test2()
    {
        VoltageDividerChannel vdc = new VoltageDividerChannel(new FixedVoltage(3.3), 1700, 3300);
        assertEquals(5, vdc.getAsDouble(), Epsilon);
    }
    
}
