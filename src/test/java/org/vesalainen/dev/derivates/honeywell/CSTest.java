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
package org.vesalainen.dev.derivates.honeywell;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.vesalainen.dev.FixedVoltage;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class CSTest
{
    private static final double Epsilon = 1e-10;
    public CSTest()
    {
    }

    @Test
    public void test1() throws IOException
    {
        CS cs = new CS(57, 1, new FixedVoltage(6), new FixedVoltage(12), false);
        assertEquals(0.0, cs.getAsDouble(), Epsilon);
    }
    
    @Test
    public void test2() throws IOException
    {
        CS cs = new CS(57, 1, new FixedVoltage(9), new FixedVoltage(12), false);
        assertEquals(57.0, cs.getAsDouble(), Epsilon);
    }
    
    @Test
    public void test3() throws IOException
    {
        CS cs = new CS(57, 1, new FixedVoltage(3), new FixedVoltage(12), true);
        assertEquals(57.0, cs.getAsDouble(), Epsilon);
    }
    
    @Test
    public void test4() throws IOException
    {
        CS cs = new CS(57, 2, new FixedVoltage(9), new FixedVoltage(12), false);
        assertEquals(114.0, cs.getAsDouble(), Epsilon);
    }
    
}
