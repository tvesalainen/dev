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
package org.vesalainen.dev.i2c.adcpi;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vesalainen.dev.VoltageSource;
import org.vesalainen.dev.i2c.mcp342X.MCP342X.Resolution;
import org.vesalainen.lang.Primitives;
import org.vesalainen.util.LoggingCommandLine;

/**
 *
 * @author Timo Vesalainen <timo.vesalainen@iki.fi>
 */
public class Calibrator extends LoggingCommandLine
{

    public Calibrator()
    {
        addOption(int.class, "-adapter", "adapter number");
        addOption("-slave1", "slave 1 in hex (0Xxxxx)");
        addOption("-slave2", "slave 2 in hex (0Xxxxx)");
        addOption(int.class, "-channel", "channel number");
        addOption("-half", "half channel", null, -1);
        addOption("-resolution", "resolution", null, Resolution.Bits12);
        addOption("-voltage", "target voltage", null, Double.NaN);
        addOption("-slope", "channel slope", null, Double.NaN);
    }
    public static void main(String... args)
    {
        try
        {
            Calibrator cal = new Calibrator();
            cal.command(args);
            cal.process();
        }
        catch (IOException ex)
        {
            Logger.getLogger(Calibrator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void process() throws IOException
    {
        int adapter = (int) getValue("-adapter");
        short slave1 = Primitives.parseShort((CharSequence) getValue("-slave1"));
        short slave2 = Primitives.parseShort((CharSequence) getValue("-slave2"));
        int channel = (int) getValue("-channel");
        int half = (int) getValue("-half");
        Resolution resolution = (Resolution) getValue("-resolution");
        double voltage = (double) getValue("-voltage");
        double slope = (double) getValue("-slope");
        ADCPiV2 adc = ADCPiV2.open(adapter, slave1, slave2);
        VoltageSource oc = adc.getOptimizingChannel(channel, resolution);
        double mea = oc.getAsDouble();
        LineCorrectedChannel lcc;
        if (Double.isNaN(slope))
        {
            lcc = new LineCorrectedChannel(oc, mea, voltage);
        }
        else
        {
            lcc = new LineCorrectedChannel(oc, mea, slope);
        }
        double corrected = lcc.getAsDouble();
        if (Double.isNaN(slope))
        {
            System.err.println(String.format("points=\"%f %f\"", mea, voltage));
        }
        System.err.println(String.format("slope=%f", lcc.getSlope()));
        System.err.println(String.format("corrected %f diff=%f", corrected, voltage-corrected));
        if (half != -1)
        {
            VoltageSource halfChannel = adc.getOptimizingChannel(half, resolution);
            double halfMea = halfChannel.getAsDouble();
            double halfTarget = corrected/2.0;
            LineCorrectedChannel hlcc = new LineCorrectedChannel(halfChannel, halfMea, halfTarget);
            double halfCorrected = hlcc.getAsDouble();
            System.err.println(String.format("half points=\"%f %f\"", halfMea, halfTarget));
            System.err.println(String.format("half slope=%f", hlcc.getSlope()));
            System.err.println(String.format("half corrected %f diff=%f", halfCorrected, halfTarget-halfCorrected));
        }
    }
}
