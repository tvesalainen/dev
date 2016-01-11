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
package org.vesalainen.dev.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vesalainen.dev.FileIO;

/**
 *
 * @author tkv
 */
public class Test1
{
    public static void test1()
    {
        try (FileIO fio = FileIO.open("test1.bin"))
        {
            fio.write((byte)66);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (FileIO fio = FileIO.open("test1.bin"))
        {
            int cc = fio.read();
            if (cc != 66)
            {
                System.err.println(cc+" expected 66");
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void test2()
    {
        try (FileIO fio = FileIO.open("test2.bin"))
        {
            fio.write((byte)'t', (byte)'e', (byte)'s', (byte)'t');
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
        try (FileIO fio = FileIO.open("test2.bin"))
        {
            byte[] buf = new byte[8];
            int rc = fio.read(buf);
            if (rc != 4)
            {
                System.err.println(rc+" expected 4");
            }
            String s = new String(buf, 0, 4);
            if (!"test".equals(s))
            {
                System.err.println(s+" expected 'test'");
            }
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
