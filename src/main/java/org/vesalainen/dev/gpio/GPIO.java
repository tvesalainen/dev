/*
 * Copyright (C) 2017 tkv
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
package org.vesalainen.dev.gpio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.vesalainen.loader.LibraryLoader;
import org.vesalainen.loader.LibraryLoader.OS;

/**
 * GPIO class represent one General Purpose Input/Output pin. Each pin has number
 * and it is either input or output.
 * @author tkv
 */
public abstract class GPIO implements AutoCloseable
{
    protected int number;
    protected boolean output;

    public GPIO(int number, boolean out)
    {
        this.number = number;
        this.output = out;
    }
    /**
     * Returns a new GPIO instance
     * @param number Number of pin
     * @param output If true the pin is output.
     * @return
     * @throws IOException 
     */
    public static GPIO newInstance(int number, boolean output) throws IOException
    {
        OS os = LibraryLoader.getOS();
        switch (os)
        {
            case Linux:
                GPIO gpio = new LinuxGPIO(number, output);
                gpio.doInit();
                gpio.doSetIO(output);
                return gpio;
            default:
                throw new UnsupportedOperationException(os+" not supported");
        }
    }
    /**
     * Returns the pin number.
     * @return 
     */
    public int getNumber()
    {
        return number;
    }
    /**
     * Set pin direction.
     * @param output
     * @throws IOException 
     */
    public void setIO(boolean output) throws IOException
    {
        if (this.output != output)
        {
            doSetIO(output);
        }
    }
    /**
     * Returns the pins state. True = high, false = low.
     * @return
     * @throws IOException 
     */
    public boolean test() throws IOException
    {
        if (!output)
        {
            return doTest();
        }
        else
        {
            throw new IllegalStateException("test in out mode");
        }
    }
    /**
     * Sets pins state.
     * @param high
     * @throws IOException 
     */
    public void set(boolean high) throws IOException
    {
        if (output)
        {
            doSet(high);
        }
        else
        {
            throw new IllegalStateException("set in in mode");
        }
    }
    protected abstract void doInit() throws IOException;
    
    protected abstract void doSetIO(boolean out) throws IOException;
    
    protected abstract boolean doTest() throws IOException;
    
    protected abstract void doSet(boolean high) throws IOException;
    
    private static class LinuxGPIO extends GPIO
    {
        private static final byte[] OUT = "out".getBytes();
        private static final byte[] IN = "in".getBytes();
        private byte[] bytes;
        private String dirFilename;
        private String valueFilename;
        private FileInputStream in;
        private FileOutputStream out;

        public LinuxGPIO(int number, boolean out)
        {
            super(number, out);
            this.bytes = String.valueOf(number).getBytes();
            this.dirFilename = String.format("/sys/class/gpio/gpio%d/direction", number);
            this.valueFilename = String.format("/sys/class/gpio/gpio%d/value", number);
        }

        @Override
        protected void doInit() throws IOException
        {
            try (FileOutputStream exp = new FileOutputStream("/sys/class/gpio/export"))
            {
                exp.write(bytes);
            }
        }

        @Override
        protected void doSetIO(boolean output) throws IOException
        {
            try (FileOutputStream fs = new FileOutputStream(dirFilename))
            {
                if (output)
                {
                    fs.write(OUT);
                    if (in != null)
                    {
                        in.close();
                        in = null;
                    }
                    out = new FileOutputStream(valueFilename);
                }
                else
                {
                    if (out != null)
                    {
                        out.close();
                        out = null;
                    }
                    fs.write(IN);
                    in = new FileInputStream(valueFilename);
                }
            }
        }

        @Override
        protected boolean doTest() throws IOException
        {
            assert in != null;
            int cc = in.read();
            switch (cc)
            {
                case '1':
                    return true;
                case '0':
                    return false;
                default:
                    throw new IOException("unexpected input "+cc);
            }
        }

        @Override
        protected void doSet(boolean high) throws IOException
        {
            assert out != null;
            if (high)
            {
                out.write('1');
            }
            else
            {
                out.write('0');
            }
        }

        @Override
        public void close() throws Exception
        {
            if (output)
            {
                assert out != null;
                out.close();
            }
            else
            {
                assert in != null;
                in.close();
            }
            try (FileOutputStream exp = new FileOutputStream("/sys/class/gpio/unexport"))
            {
                exp.write(bytes);
            }
        }
        
    }
}
