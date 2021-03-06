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

import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.vesalainen.loader.LibraryLoader;
import static org.vesalainen.loader.LibraryLoader.OS.Linux;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class MeterTest
{
    
    public MeterTest()
    {
    }

    @Test
    public void test1() throws IOException
    {
        if (LibraryLoader.getOS() == Linux)
        {
            File file = new File("src/test/resources/dev-config.xml");
            DevMeter meter = new DevMeter(file);
        }
    }
    
}
