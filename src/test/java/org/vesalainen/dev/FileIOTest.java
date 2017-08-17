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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.vesalainen.loader.LibraryLoader;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class FileIOTest
{
    
    public FileIOTest()
    {
    }

    @Test
    public void test1()
    {
        LibraryLoader.OS os = LibraryLoader.getOS();
        if (LibraryLoader.OS.Linux == os)
        {
            try
            {
                URL url = FileIOTest.class.getResource("/test.txt");
                File file = new File(url.toURI());

                try (FileIO fio = FileIO.open(file.getPath()))
                {

                }
                catch (IOException ex)
                {
                    Logger.getLogger(FileIOTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail(ex.getMessage());
                }
            }
            catch (URISyntaxException ex)
            {
                Logger.getLogger(FileIOTest.class.getName()).log(Level.SEVERE, null, ex);
                fail(ex.getMessage());
            }
        }
        else
        {
            System.err.println(os+" not supported");
        }
    }
    
}
