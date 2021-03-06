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

import org.vesalainen.dev.CurrentSource;
import org.vesalainen.dev.VoltageSource;
import org.vesalainen.util.logging.JavaLogging;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class CS extends JavaLogging implements CurrentSource
{
    protected double maxAmps;
    protected double turns;
    protected VoltageSource measured;
    protected VoltageSource reference;
    protected double sign;

    public CS(double maxAmps)
    {
        super(CS.class);
        this.maxAmps = maxAmps;
    }

    public CS(double maxAmps, double turns, VoltageSource measured, VoltageSource reference, boolean negative)
    {
        super(CS.class);
        this.maxAmps = maxAmps;
        this.turns = turns;
        this.measured = measured;
        this.reference = reference;
        this.sign = negative ? -1.0 : 1.0;
    }
    
    @Override
    public double getAsDouble()
    {
        double ref = reference.getAsDouble();
        double mea = measured.getAsDouble();
        fine("%s: ref %f mea %f", this.getClass().getSimpleName(), ref, mea);
        return sign*turns*(mea - ref/2.0)*(maxAmps/(ref/4.0));
    }

    public void setTurns(double turns)
    {
        this.turns = turns;
    }

    public void setMeasured(VoltageSource measured)
    {
        this.measured = measured;
    }

    public void setReference(VoltageSource reference)
    {
        this.reference = reference;
    }

    public void setNegative(boolean negative)
    {
        this.sign = negative ? -1.0 : 1.0;
    }

    @Override
    public double min()
    {
        return -maxAmps/turns;
    }

    @Override
    public double max()
    {
        return maxAmps/turns;
    }

    @Override
    public String toString()
    {
        return "CS{" + "maxAmps=" + maxAmps + ", turns=" + turns + ", measured=" + measured + ", reference=" + reference + ", sign=" + sign + '}';
    }
    
}
