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
package org.vesalainen.dev.i2c.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vesalainen.dev.FileIO;
import org.vesalainen.dev.i2c.I2CAdapter;
import org.vesalainen.dev.i2c.I2CSMBus;
import org.vesalainen.dev.i2c.adcpi.ADCPiV2;
import org.vesalainen.dev.i2c.mcp342X.MCP3422;
import org.vesalainen.dev.i2c.mcp342X.MCP342X;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import org.vesalainen.dev.i2c.mcp342X.MCP342XChannel;

/**
 *
 * @author tkv
 */
public class Test1
{
    public static void test1()
    {
        try (I2CAdapter i2c = I2CAdapter.open(1))
        {
            i2c.setAddress((short)0x68);
            System.err.println(i2c.getFunctionality());
            i2c.setPEC(false);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void test2()
    {
        try (I2CSMBus bus = I2CSMBus.open(1))
        {
            MCP3422 mcp = new MCP3422(bus);
            for (int r=0;r<4;r++)
            {
                Resolution res = Resolution.values()[r];
                for (int g=0;g<4;g++)
                {
                    Gain gain = Gain.values()[g];
                    double measure = mcp.measure(1, res, gain);
                    System.err.println(res+" "+gain+" V="+measure);
                }
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void test3()
    {
        
        try (ADCPiV2 adcpi = ADCPiV2.open(1, (short)0x68, (short)0x69))
        {
            MCP342XChannel cursens = adcpi.getOptimizingChannel(1);
            MCP342XChannel bat = adcpi.getOptimizingVoltageDividerChannel(2, 56000.0);
            MCP342XChannel startBat = adcpi.getVoltageDividerChannel(3, Resolution.Bits18, Gain.X1, 56000.0);
            MCP342XChannel panel = adcpi.getOptimizingVoltageDividerChannel(4, 68000.0);
            System.err.printf("cur=%f ", cursens.measure());
            System.err.printf("bat=%f ", bat.measure());
            System.err.printf("str=%f ", startBat.measure());
            System.err.printf("pan=%f ", panel.measure());
            System.err.println();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void test4()
    {
        
        try (ADCPiV2 adcpi = ADCPiV2.open(1, (short)0x68, (short)0x69))
        {
            MCP342XChannel cursens = adcpi.getLineCorrectedChannel(1, Resolution.Bits18, Gain.X1, 2.44, 0, 4.88, 40);
            for (int ii=0;ii<100;ii++)
            {
                System.err.printf("cur=%f ", cursens.measure());
                System.err.println();
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void main(String... args)
    {
        FileIO.setDebug(true);
        //test1();
        //test2();
        test4();
    }
}
