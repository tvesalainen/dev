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
package org.vesalainen.dev.i2c.gcbc;

import java.io.IOException;
import org.vesalainen.dev.VoltageSource;
import org.vesalainen.math.AbstractLine;
import org.vesalainen.math.Line;

/**
 *
 * @author tkv
 */
public class GCBC0401A
{
    private final VoltageSource reference;
    private final VoltageSource measured;
    private final AbstractLine line;

    public GCBC0401A(VoltageSource reference, VoltageSource measured)
    {
        this.reference = reference;
        this.measured = measured;
        this.line = new AbstractLine(2.5, 0, 4.5, 40.0);
    }
    
    public double current() throws IOException
    {
        double ref = reference.voltage();
        double mea = measured.voltage();
        line.set(ref/2.0, 0, ref+2.0, 40.0);
        return line.getY(mea);
    }
}
