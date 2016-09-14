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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.vesalainen.code.PropertySetter;
import org.vesalainen.loader.LibraryLoader;
import static org.vesalainen.loader.LibraryLoader.OS.Linux;
import org.vesalainen.math.UnitType;
import org.vesalainen.util.HashMapList;
import org.vesalainen.util.MapList;
import org.vesalainen.util.logging.JavaLogging;

/**
 *
 * @author tkv
 */
public abstract class AbstractMeter extends JavaLogging
{
    
    protected Timer timer;
    protected Map<Long, AbstractTask> taskMap = new HashMap<>();
    protected ReentrantLock lock = new ReentrantLock();

    public AbstractMeter()
    {
        super(AbstractMeter.class);
    }

    public static AbstractMeter getInstance(File devConfigFile) throws IOException
    {
        if (LibraryLoader.getOS() == Linux)
        {
            JavaLogging.getLogger(AbstractMeter.class).config("meter from %s", devConfigFile);
            return new DevMeter(devConfigFile);
        }
        else
        {
            JavaLogging.getLogger(AbstractMeter.class).config("meter as simulator");
            return new MeterSimulator();
        }
    }
    public abstract UnitType getUnit(String name);
    
    public abstract double getMin(String name);
    
    public abstract double getMax(String name);
    
    public abstract Set<String> getNames();

    public abstract double meter(String name);

    public void register(PropertySetter observer, String property, long period, TimeUnit unit)
    {
        fine("register(%s %d %s)", property, period, unit);
        lock.lock();
        try
        {
            if (timer == null)
            {
                finer("started timer");
                timer = new Timer(DevMeter.class.getSimpleName());
            }
            AbstractTask task = taskMap.get(period);
            if (task == null)
            {
                finer("created task for %d %s", period, unit);
                task = createTask();
                timer.scheduleAtFixedRate(task, 100, unit.toMillis(period));
                taskMap.put(period, task);
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
        fine("unregister(%s)", property);
        lock.lock();
        try
        {
            Iterator<Map.Entry<Long, AbstractTask>> it = taskMap.entrySet().iterator();
            while (it.hasNext())
            {
                Entry<Long, AbstractTask> e = it.next();
                AbstractTask task = e.getValue();
                if (task.remove(observer, property))
                {
                    finer("removed %s from task %d", property, e.getKey());
                    if (task.isEmpty())
                    {
                        finer("removed task %d", e.getKey());
                        task.cancel();
                        it.remove();
                    }
                    break;
                }
            }
            if (taskMap.isEmpty())
            {
                finer("cancel timer");
                timer.cancel();
                timer = null;
            }
        }
        finally
        {
            lock.unlock();
        }
    }
    protected abstract AbstractTask createTask();
    
    protected abstract class AbstractTask extends TimerTask
    {
        protected final MapList<String,PropertySetter> mapList = new HashMapList<>();
        
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
    }
}
