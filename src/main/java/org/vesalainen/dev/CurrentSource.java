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
package org.vesalainen.dev;

import org.vesalainen.math.Unit;
import org.vesalainen.math.UnitType;
import static org.vesalainen.math.UnitType.Ampere;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
@Unit(Ampere)
public interface CurrentSource extends Source
{
    @Override
    public default UnitType type()
    {
        return UnitType.Ampere;
    }
    
}
