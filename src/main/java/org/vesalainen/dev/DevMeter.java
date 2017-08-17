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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.DoubleSupplier;
import java.util.logging.Level;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.vesalainen.code.DoubleFire;
import org.vesalainen.code.PropertySetter;
import org.vesalainen.dev.derivates.honeywell.CS;
import org.vesalainen.dev.derivates.honeywell.CSLA1GD;
import org.vesalainen.dev.derivates.honeywell.CSLH3A9;
import org.vesalainen.dev.i2c.adcpi.ADCPiV2;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Gain;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import org.vesalainen.dev.jaxb.ADCPiV2Type;
import org.vesalainen.dev.jaxb.ADCPiV2Type.Channel;
import org.vesalainen.dev.jaxb.Dev;
import org.vesalainen.dev.jaxb.Dev.Derivates;
import org.vesalainen.dev.jaxb.Dev.Log;
import org.vesalainen.dev.jaxb.HoneywellCS;
import org.vesalainen.dev.jaxb.I2CType;
import org.vesalainen.dev.jaxb.Mcp342XGain;
import org.vesalainen.dev.jaxb.SourceType;
import org.vesalainen.lang.Primitives;
import org.vesalainen.math.UnitType;
import org.vesalainen.util.DoubleMap;
import org.vesalainen.util.logging.BaseLogging;
import org.vesalainen.util.logging.JavaLogging;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class DevMeter extends AbstractMeter
{
    private final Map<String,Source> map = new HashMap<>();
    private final DoubleMap<String> minMap = new DoubleMap<>();
    private final DoubleMap<String> maxMap = new DoubleMap<>();

    protected DevMeter(File devConfig) throws IOException
    {
        try
        {
            JAXBContext jaxbCtx = JAXBContext.newInstance("org.vesalainen.dev.jaxb");
            Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            Dev dev = (Dev) unmarshaller.unmarshal(devConfig);
            for (I2CType i2cType : dev.getI2C())
            {
                i2c(i2cType);
            }
            derivates(dev.getDerivates());
            for (Log log : dev.getLog())
            {
                log(log);
            }
        }
        catch (JAXBException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Set<String> getNames()
    {
        return map.keySet();
    }

    @Override
    public UnitType getUnit(String name)
    {
        Source source = map.get(name);
        if (source == null)
        {
            throw new IllegalArgumentException(name+" not found");
        }
        return source.type();
    }

    @Override
    public double getMin(String name)
    {
        if (minMap.containsKey(name))
        {
            return minMap.getDouble(name);
        }
        Source source = map.get(name);
        if (source == null)
        {
            throw new IllegalArgumentException(name+" not found");
        }
        return source.min();
    }

    @Override
    public double getMax(String name)
    {
        if (maxMap.containsKey(name))
        {
            return maxMap.getDouble(name);
        }
        Source source = map.get(name);
        if (source == null)
        {
            throw new IllegalArgumentException(name+" not found");
        }
        return source.max();
    }

    @Override
    public double meter(String name)
    {
        DoubleSupplier supplier = map.get(name);
        if (supplier == null)
        {
            throw new IllegalArgumentException(name + " not found");
        }
        double result = supplier.getAsDouble();
        info("%s = %f", name, result);
        return result;
    }

    
    private void i2c(I2CType i2cType) throws IOException
    {
        int adapter = i2cType.getAdapter();
        for (ADCPiV2Type adcPiV2Type : i2cType.getADCPiV2())
        {
            adcPiV2(adapter, adcPiV2Type);
        }
    }

    private void adcPiV2(int adapter, ADCPiV2Type adcPiV2Type) throws IOException
    {
        short slave1 = Primitives.parseShort(adcPiV2Type.getSlave1());
        short slave2 = Primitives.parseShort(adcPiV2Type.getSlave2());
        ADCPiV2 adcPiV2 = ADCPiV2.open(adapter, slave1, slave2);
        for (Channel channel : adcPiV2Type.getChannel())
        {
            channel(adcPiV2, channel);
        }
    }
    
    private void channel(ADCPiV2 adcPiV2, Channel channel)
    {
        int channelNumber = channel.getChannel();
        String name = channel.getName();
        handleLimits(channel);
        double[] points = null;
        List<Double> pointList = channel.getPoints();
        if (pointList != null && !pointList.isEmpty())
        {
            points = new double[pointList.size()];
            for (int ii=0;ii<points.length;ii++)
            {
                points[ii] = pointList.get(ii);
            }
        }
        double resistor = channel.getResistor();
        Mcp342XGain gain = channel.getGain();
        Resolution resolution = Resolution.values()[channel.getResolution().ordinal()];
        config("channel(%s, %d, %f, %s, %s)", name, channelNumber, resistor, gain, resolution);
        if (gain == null)
        {
            if (points == null)
            {
                map.put(name, adcPiV2.getOptimizingVoltageDividerChannel(channelNumber, resolution, resistor));
            }
            else
            {
                map.put(name, adcPiV2.getOptimizingLineCorrectedChannel(channelNumber, resolution, points));
            }
        }
        else
        {
            if (points == null)
            {
                map.put(name, adcPiV2.getVoltageDividerChannel(channelNumber, Resolution.valueOf(resolution.name()), Gain.valueOf(gain.name()), resistor));
            }
            else
            {
                map.put(name, adcPiV2.getLineCorrectedChannel(channelNumber, Resolution.valueOf(resolution.name()), Gain.valueOf(gain.name()), points));
            }
        }
    }

    private void derivates(Derivates derivates)
    {
        if (derivates != null)
        {
            for (HoneywellCS cs : derivates.getCSLA1GD())
            {
                populate(new CSLA1GD(), cs);
            }
            for (HoneywellCS cs : derivates.getCSLH3A9())
            {
                populate(new CSLH3A9(), cs);
            }
        }
    }

    private void populate(CS cs, HoneywellCS csType)
    {
        String name = csType.getName();
        handleLimits(csType);
        if (map.containsKey(name))
        {
            throw new IllegalArgumentException(name+" is already defined");
        }
        String measureReference = csType.getMeasureReference();
        DoubleSupplier measureSupplier = map.get(measureReference);
        if (measureSupplier == null)
        {
            throw new IllegalArgumentException(measureReference+" is not defined for "+name);
        }
        cs.setMeasured((VoltageSource) measureSupplier);
        DoubleSupplier referenceVoltageSupplier = null;
        String referenceVoltageReference = csType.getReferenceVoltageReference();
        if (referenceVoltageReference == null)
        {
            Double referenceVoltage = csType.getReferenceVoltage();
            if (referenceVoltage == null)
            {
                throw new IllegalArgumentException("no reference voltage defined for "+name);
            }
            else
            {
                referenceVoltageSupplier = new FixedVoltage(referenceVoltage);
            }
        }
        else
        {
            referenceVoltageSupplier = map.get(referenceVoltageReference);
            if (referenceVoltageSupplier == null)
            {
                throw new IllegalArgumentException(referenceVoltageReference+" is not defined for "+name);
            }
        }
        cs.setReference((VoltageSource) referenceVoltageSupplier);
        cs.setTurns(csType.getTurns());
        cs.setNegative(csType.isNegative());
        config("add %s", cs);
        map.put(name, cs);
    }

    @Override
    protected AbstractTask createTask()
    {
        return new Task();
    }

    private void log(Log log)
    {
        LogHandler logHandler = new LogHandler(log);
        for (String meter : log.getMeters())
        {
            register(logHandler, meter, log.getPeriod().longValue(), TimeUnit.MILLISECONDS);
        }
    }

    private void handleLimits(SourceType sourceType)
    {
        Double min = sourceType.getMin();
        if (min != null)
        {
            minMap.put(sourceType.getName(), min);
        }
        Double max = sourceType.getMax();
        if (max != null)
        {
            maxMap.put(sourceType.getName(), max);
        }
    }

    private class LogHandler extends JavaLogging implements DoubleFire
    {
        private final String format;
        private final Level level;
        private final List<String> meters;
        private final int size;
        private final DoubleMap<String> map = new DoubleMap<>();
        private final Object[] array;
        private int count;

        public LogHandler(Log log)
        {
            super(log.getLogger());
            this.level = BaseLogging.parseLevel(log.getLevel());
            this.format = log.getFormat();
            this.meters = log.getMeters();
            this.size = meters.size();
            int ii=0;
            for (String property : meters)
            {
                map.put(property, 0);
            }
            array = new Object[size];
        }
        
        @Override
        public void fire(String property, double value)
        {
            map.put(property, value);
            count++;
            if (count % size == 0)
            {
                int ii=0;
                for (String meter : meters)
                {
                    array[ii++] = map.getDouble(meter);
                }
                log(level, format, array);
            }
        }
        
    }
    private class Task extends AbstractTask
    {
        @Override
        public void run()
        {
            for (Entry<String, List<PropertySetter>> e : mapList.entrySet())
            {
                String key = e.getKey();
                DoubleSupplier supplier = map.get(key);
                if (supplier == null)
                {
                    throw new IllegalArgumentException("no supplier for "+key);
                }
                double value = supplier.getAsDouble();
                List<PropertySetter> list = e.getValue();
                finer("%s %f for %d", supplier, value, list.size());
                list.stream().forEach((PropertySetter ps)->ps.set(key, value));
            }
        }
        
    }
}
