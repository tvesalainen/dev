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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.DoubleSupplier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
import org.vesalainen.dev.jaxb.HoneywellCS;
import org.vesalainen.dev.jaxb.I2CType;
import org.vesalainen.dev.jaxb.Mcp342XGain;
import org.vesalainen.dev.jaxb.Mcp342XResolution;
import org.vesalainen.lang.Primitives;
import org.vesalainen.util.HashMapList;
import org.vesalainen.util.MapList;

/**
 *
 * @author tkv
 */
public class DevMeter
{
    private Map<String,DoubleSupplier> map = new HashMap<>();
    private Timer timer;
    private Map<Long,Task> taskMap = new HashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    public DevMeter(File devConfig) throws IOException
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
        }
        catch (JAXBException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public double meter(String name)
    {
        DoubleSupplier supplier = map.get(name);
        if (supplier == null)
        {
            throw new IllegalArgumentException(name+" not found");
        }
        return supplier.getAsDouble();
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
        int channelNumber = channel.getChannel()-1; // ADCPiV2 pins are numbered 1 - 8
        String name = channel.getName();
        double resistor = channel.getResistor();
        Mcp342XGain gain = channel.getGain();
        Mcp342XResolution resolution = channel.getResolution();
        if (gain == null || resolution == null)
        {
            map.put(name, adcPiV2.getOptimizingVoltageDividerChannel(channelNumber, resistor));
        }
        else
        {
            map.put(name, adcPiV2.getVoltageDividerChannel(channelNumber, Resolution.valueOf(resolution.name()), Gain.valueOf(gain.name()), resistor));
        }
    }

    private void derivates(Derivates derivates)
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

    private void populate(CS cs, HoneywellCS csType)
    {
        String name = csType.getName();
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
        map.put(name, cs);
    }

    public void register(PropertySetter observer, String property, long period, TimeUnit unit)
    {
        lock.lock();
        try
        {
            if (timer == null)
            {
                timer = new Timer(DevMeter.class.getSimpleName());
            }
            Task task = taskMap.get(period);
            if (task == null)
            {
                task = new Task();
                timer.scheduleAtFixedRate(task, 100, unit.toMillis(period));
            }
            task.add(observer, property);
        }
        finally
        {
            lock.unlock();
        }
    }
    public void unregister(PropertySetter observer, String property)
    {
        lock.lock();
        try
        {
            Iterator<Entry<Long,Task>> it = taskMap.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<Long, Task> e = it.next();
                Task task = e.getValue();
                if (task.remove(observer, property))
                {
                    if (task.isEmpty())
                    {
                        task.cancel();
                        it.remove();
                    }
                    break;
                }
            }
            if (taskMap.isEmpty())
            {
                timer.cancel();
                timer = null;
            }
        }
        finally
        {
            lock.unlock();
        }
    }
    private class Task extends TimerTask
    {
        private final MapList<String,PropertySetter> mapList = new HashMapList<>();
        
        private void add(PropertySetter observer, String property)
        {
            mapList.add(property, observer);
        }
        private boolean remove(PropertySetter observer, String property)
        {
            return mapList.removeItem(property, observer);
        }
        private boolean isEmpty()
        {
            return mapList.isEmpty();
        }
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
                e.getValue().stream().forEach((PropertySetter ps)->ps.set(key, value));
            }
        }
        
    }
}
