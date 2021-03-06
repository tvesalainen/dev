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
package org.vesalainen.dev.i2c.mcp342X;

import org.vesalainen.dev.i2c.mcp342X.MCP342X;
import org.junit.Test;
import static org.junit.Assert.*;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.SampleRate;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class MCP342XTest
{
    static final double Epsilon = 1e-10;
    public MCP342XTest()
    {
    }

    @Test
    public void test1()
    {
        MCP342X m = new MCP342X();
        
        m.setReady(true);
        assertTrue(m.isReady());
        assertEquals((byte)0b10000000, m.getConfig());
        
        m.setReady(false);
        assertFalse(m.isReady());
        assertEquals((byte)0b00000000, m.getConfig());
        
        m.setChannel(1);
        assertEquals(1, m.getChannel());
        assertEquals((byte)0b00000000, m.getConfig());
        
        m.setChannel(2);
        assertEquals(2, m.getChannel());
        assertEquals((byte)0b00100000, m.getConfig());
        
        m.setChannel(3);
        assertEquals(3, m.getChannel());
        assertEquals((byte)0b01000000, m.getConfig());
        
        m.setChannel(4);
        assertEquals(4, m.getChannel());
        assertEquals((byte)0b01100000, m.getConfig());
        
        m.setContinousConversion(true);
        assertTrue(m.getContinousConversion());
        assertEquals((byte)0b01110000, m.getConfig());
        
        m.setContinousConversion(false);
        assertFalse(m.getContinousConversion());
        assertEquals((byte)0b01100000, m.getConfig());
        
        m.setSampleRate(MCP342X.SampleRate.SPS240);
        assertEquals(SampleRate.SPS240, m.getSampleRate());
        assertEquals(Resolution.Bits12, m.getResolution());
        assertEquals((byte)0b01100000, m.getConfig());
        
        m.setResolution(Resolution.Bits14);
        assertEquals(SampleRate.SPS60, m.getSampleRate());
        assertEquals(Resolution.Bits14, m.getResolution());
        assertEquals((byte)0b01100100, m.getConfig());
        
        m.setSampleRate(MCP342X.SampleRate.SPS15);
        assertEquals(SampleRate.SPS15, m.getSampleRate());
        assertEquals(Resolution.Bits16, m.getResolution());
        assertEquals((byte)0b01101000, m.getConfig());
        
        m.setResolution(Resolution.Bits18);
        assertEquals(SampleRate.SPS3_75, m.getSampleRate());
        assertEquals(Resolution.Bits18, m.getResolution());
        assertEquals((byte)0b01101100, m.getConfig());
        
        m.setGain(MCP342X.Gain.X1);
        assertEquals(Gain.X1, m.getGain());
        assertEquals(1, m.getPGA(), Epsilon);
        assertEquals((byte)0b01101100, m.getConfig());
        
        m.setGain(MCP342X.Gain.X2);
        assertEquals(Gain.X2, m.getGain());
        assertEquals(2, m.getPGA(), Epsilon);
        assertEquals((byte)0b01101101, m.getConfig());
        
        m.setGain(MCP342X.Gain.X4);
        assertEquals(Gain.X4, m.getGain());
        assertEquals(4, m.getPGA(), Epsilon);
        assertEquals((byte)0b01101110, m.getConfig());
        
        m.setGain(MCP342X.Gain.X8);
        assertEquals(Gain.X8, m.getGain());
        assertEquals(8, m.getPGA(), Epsilon);
        assertEquals((byte)0b01101111, m.getConfig());
    }
    
    @Test
    public void test18()
    {
        MCP342X m = new MCP342X();
        m.setResolution(Resolution.Bits18);
        assertEquals(15.625e-6, m.getLSB(), Epsilon);
        double v = m.getVoltage((byte)0b01, (byte)0b11111111, (byte)0b11111111);
        assertEquals(Double.POSITIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b11111111, (byte)0b11111111, (byte)0b11111111);
        assertEquals(Double.NEGATIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b11, (byte)0b11111111, (byte)0b11111111);
        assertEquals(Double.NEGATIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b00, (byte)0b00000000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b10, (byte)0b00000000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b00, (byte)0b00000000, (byte)0b00000001);
        assertEquals(m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b10, (byte)0b00000000, (byte)0b00000001);
        assertEquals(-m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00, (byte)0b00000000, (byte)0b00000010);
        assertEquals(2*m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b01, (byte)0b00000000, (byte)0b00000000);
        assertEquals(65536*m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00, (byte)0b00000001, (byte)0b00000000);
        assertEquals(256*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X2);
        v = m.getVoltage((byte)0b00, (byte)0b00000001, (byte)0b00000000);
        assertEquals(128*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X4);
        v = m.getVoltage((byte)0b00, (byte)0b00000001, (byte)0b00000000);
        assertEquals(64*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X8);
        v = m.getVoltage((byte)0b00, (byte)0b00000001, (byte)0b00000000);
        assertEquals(32*m.getLSB(), v, Epsilon);
    }
    
    @Test
    public void test16()
    {
        MCP342X m = new MCP342X();
        m.setResolution(Resolution.Bits16);
        assertEquals(62.5e-6, m.getLSB(), Epsilon);
        double v = m.getVoltage((byte)0b01111111, (byte)0b11111111);
        assertEquals(Double.POSITIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b11111111, (byte)0b11111111);
        assertEquals(Double.NEGATIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b10000000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000001);
        assertEquals(m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b10000000, (byte)0b00000001);
        assertEquals(-m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000010);
        assertEquals(2*m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(256*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X2);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(128*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X4);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(64*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X8);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(32*m.getLSB(), v, Epsilon);
    }
    
    @Test
    public void test14()
    {
        MCP342X m = new MCP342X();
        m.setResolution(Resolution.Bits14);
        assertEquals(250e-6, m.getLSB(), Epsilon);
        double v = m.getVoltage((byte)0b00011111, (byte)0b11111111);
        assertEquals(Double.POSITIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b00111111, (byte)0b11111111);
        assertEquals(Double.NEGATIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b11111111, (byte)0b11111111);
        assertEquals(Double.NEGATIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b00100000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000001);
        assertEquals(m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00100000, (byte)0b00000001);
        assertEquals(-m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000010);
        assertEquals(2*m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(256*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X2);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(128*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X4);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(64*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X8);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(32*m.getLSB(), v, Epsilon);
    }
    
    @Test
    public void test12()
    {
        MCP342X m = new MCP342X();
        m.setResolution(Resolution.Bits12);
        assertEquals(1e-3, m.getLSB(), Epsilon);
        double v = m.getVoltage((byte)0b00000111, (byte)0b11111111);
        assertEquals(Double.POSITIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b00001111, (byte)0b11111111);
        assertEquals(Double.NEGATIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b11111111, (byte)0b11111111);
        assertEquals(Double.NEGATIVE_INFINITY, v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b00001000, (byte)0b00000000);
        assertEquals(0, v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000001);
        assertEquals(m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00001000, (byte)0b00000001);
        assertEquals(-m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00000000, (byte)0b00000010);
        assertEquals(2*m.getLSB(), v, Epsilon);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(256*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X2);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(128*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X4);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(64*m.getLSB(), v, Epsilon);
        m.setGain(Gain.X8);
        v = m.getVoltage((byte)0b00000001, (byte)0b00000000);
        assertEquals(32*m.getLSB(), v, Epsilon);
    }
    
}
