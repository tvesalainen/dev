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
package org.vesalainen.dev;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.vesalainen.code.PropertySetter;
import org.vesalainen.math.UnitType;

/**
 *
 * @author tkv
 */
public class MeterSimulator extends AbstractMeter
{
    private Set<String> names = new HashSet<>();

    public MeterSimulator()
    {
        names.add("simVoltage1");
        names.add("simVoltage2");
        names.add("simCurrent1");
    }
    
    @Override
    public Set<String> getNames()
    {
        return names;
    }

    @Override
    public UnitType getUnit(String name)
    {
        return UnitType.Unitless;
    }

    @Override
    public double meter(String name)
    {
        return 12.345;
    }

    @Override
    protected AbstractTask createTask()
    {
        return new Task();
    }

    private class Task extends AbstractTask
    {
        @Override
        public void run()
        {
            for (Map.Entry<String, List<PropertySetter>> e : mapList.entrySet())
            {
                String key = e.getKey();
                double value = 12.345;
                e.getValue().stream().forEach((PropertySetter ps)->ps.set(key, value));
            }
        }
        
    }
}
