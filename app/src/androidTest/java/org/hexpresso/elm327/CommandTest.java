package org.hexpresso.elm327;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.Response;
import org.hexpresso.elm327.commands.ResponseFilter;
import org.hexpresso.elm327.commands.general.VehicleIdentifierNumberCommand;
import org.hexpresso.elm327.commands.protocol.PrintVersionIdCommand;
import org.hexpresso.elm327.commands.protocol.ReadInputVoltageCommand;
import org.hexpresso.soulevspy.obd.commands.BatteryManagementSystemCommand;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;
import org.hexpresso.soulevspy.util.ClientSharedPreferences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-24.
 */
public class CommandTest extends AndroidTestCase {

    ByteArrayInputStream input = null;
    ByteArrayOutputStream output = null;

    final String msg2101 = "7EC 10 3D 61 01 FF FF FF FF \r" +
                           "7EA 10 0E 61 01 F0 00 00 00 \r" +
                           "7EC 21 15 23 28 1E C8 03 00 \r" +
                           "7EA 21 ED 05 02 03 00 00 00 \r" +
                           "7EC 22 1E 0C DD 0E 0D 0E 0D \r" +
                           "7EA 22 00 00 00 00 00 00 00 \r" +
                           "7EC 23 0D 0D 0C 00 0F AB 34 \r" +
                           "7EC 24 AB 43 00 00 84 00 00 \r" +
                           "7EC 25 44 D4 00 00 49 F8 00 \r" +
                           "7EC 26 00 19 B3 00 00 1A EA \r" +
                           "7EC 27 00 09 EC 96 45 01 45 \r" +
                           "7EC 28 00 00 00 00 03 E8 00 \r";

    class ElmCommand extends AbstractCommand implements ResponseFilter {

        public ElmCommand() {
            super("Command");
            addResponseFilter(this);
        }
        public void doProcessResponse() {}

        @Override
        public void onResponseReceived(Response response) {
            response.getLines().clear();
        }
    }

    /**
     *
     */
    public void testBasicCommand() {
        final String response = "ABCD";
        input = new ByteArrayInputStream(response.getBytes());

        ElmCommand cmd = (ElmCommand) new ElmCommand().withAutoProcessResponse(true);
        try {
            cmd.execute(input, output);
        }
        catch(Exception e)
        {
            // ...
        }

        Assert.assertEquals("Command\r", output.toString());
        Assert.assertTrue(cmd.getResponse().getLines().isEmpty());
    }

    /**
     *
     */
    public void testBmsCommand() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        ClientSharedPreferences prefs = new ClientSharedPreferences(this.getContext());
        vals.setPreferences(prefs);

        input = new ByteArrayInputStream(msg2101.getBytes());

        BatteryManagementSystemCommand cmd = (BatteryManagementSystemCommand) new BatteryManagementSystemCommand().withAutoProcessResponse(true);
        try {
            cmd.execute(input, output);
            cmd.doProcessResponse();
        }
        catch(Exception e)
        {
            // ...
        }

        Assert.assertEquals(10.5, vals.get("battery.SOC_pct"));
    }

    public void testReadInputVoltage() {
        final String response = "12.5V";
        input = new ByteArrayInputStream(response.getBytes());

        ReadInputVoltageCommand cmd = (ReadInputVoltageCommand) new ReadInputVoltageCommand().withAutoProcessResponse(true);
        try {
            cmd.execute(input, output);
        }
        catch(Exception e)
        {
            // ...
        }

        Assert.assertEquals(12.5, cmd.getInputVoltage());
    }

    public void testVersion() {
        final String response = "ELM327 v1.5";
        input = new ByteArrayInputStream(response.getBytes());

        PrintVersionIdCommand cmd = (PrintVersionIdCommand) new PrintVersionIdCommand().withAutoProcessResponse(true);
        try {
            cmd.execute(input, output);
        }
        catch(Exception e)
        {
            // ...
        }

        Assert.assertEquals("ELM327 v1.5", cmd.getVersion());
    }

    public void testVehicleIdentificationNumber() {
        final String vin = "7EA 10 14 49 02 01 4B 4E 44 \r" +
                           "7EA 21 4A 58 33 41 45 31 47 \r" +
                           "7EA 22 37 31 32 33 34 35 36";

        input = new ByteArrayInputStream(vin.getBytes());

        VehicleIdentifierNumberCommand cmd = (VehicleIdentifierNumberCommand) new VehicleIdentifierNumberCommand().withAutoProcessResponse(true);
        try {
            cmd.execute(input, output);
        }
        catch(Exception e)
        {
            // ...
        }

        Assert.assertEquals("KNDJX3AE1G7123456", cmd.getValue());

    }

    public void testUnableToConnect() {
        final String vin = "...\rUNABLE TO CONNECT\r\r";

        input = new ByteArrayInputStream(vin.getBytes());

        VehicleIdentifierNumberCommand cmd = (VehicleIdentifierNumberCommand) new VehicleIdentifierNumberCommand().withAutoProcessResponse(true);

        boolean caughtIt = false;
        try {
            cmd.execute(input, output);
        }
        catch(Exception e)
        {
            assert(e.getMessage() == "UNABLE TO CONNECT");
            caughtIt = true;
        }
        assert(caughtIt);
    }

    /**
     *
     */
    @Override
    protected void setUp() {
        output = new ByteArrayOutputStream();
    }
}
