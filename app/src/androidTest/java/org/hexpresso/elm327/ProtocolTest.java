package org.hexpresso.elm327;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import org.hexpresso.elm327.commands.general.VehicleIdentifierNumberCommand;
import org.hexpresso.elm327.io.Message;
import org.hexpresso.elm327.io.Protocol;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-11-01.
 */
public class ProtocolTest extends AndroidTestCase {

    public void testProtocolVinCommand() throws Exception {
        CurrentValuesSingleton mValues = CurrentValuesSingleton.reset();

        Protocol protocol = new Protocol();
        protocol.registerOnMessageReceivedListener(new Protocol.MessageReceivedListener() {
            @Override
            public void onMessageReceived(Message message) {
                if(message.getCommand() instanceof VehicleIdentifierNumberCommand) {
                    VehicleIdentifierNumberCommand cmd = (VehicleIdentifierNumberCommand)message.getCommand();
                    Assert.assertEquals("KNDJX3AE1G7123456", cmd.getValue());
                }
            }
        });

        final String vin = "7EA 10 14 49 02 01 4B 4E 44 \r" +
                           "7EA 21 4A 58 33 41 45 31 47 \r" +
                           "7EA 22 37 31 32 33 34 35 36 \r" +
                "> ";

        ByteArrayInputStream input = new ByteArrayInputStream(vin.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        VehicleIdentifierNumberCommand vehicleIdentifierNumberCommand = new VehicleIdentifierNumberCommand();

        protocol.addCommand(vehicleIdentifierNumberCommand);
        protocol.start(input, output);

        Thread.sleep(20);

        vehicleIdentifierNumberCommand.doProcessResponse();

        assertEquals("KNDJX3AE1G7123456", CurrentValuesSingleton.getInstance().get("VIN"));
    }

    public void testProtocolVinCommand_Ionic() throws Exception {
        CurrentValuesSingleton mValues = CurrentValuesSingleton.reset();

        Protocol protocol = new Protocol();
        protocol.registerOnMessageReceivedListener(new Protocol.MessageReceivedListener() {
            @Override
            public void onMessageReceived(Message message) {
                if(message.getCommand() instanceof VehicleIdentifierNumberCommand) {
                    VehicleIdentifierNumberCommand cmd = (VehicleIdentifierNumberCommand)message.getCommand();
                    Assert.assertEquals("error: MHC\u007F\u001A\u0012", cmd.getValue());
                }
            }
        });

        final String ionicResponseTo0902 =
                "7EA 10 13 5A 90 4B 4D 48 43\r" +
                        "7EA 03 7F 1A 12\r" +
                        "> ";

        ByteArrayInputStream input = new ByteArrayInputStream(ionicResponseTo0902.getBytes());
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        VehicleIdentifierNumberCommand vehicleIdentifierNumberCommand = new VehicleIdentifierNumberCommand();

        protocol.addCommand(vehicleIdentifierNumberCommand);
        protocol.start(input, output);

        Thread.sleep(20);

        vehicleIdentifierNumberCommand.doProcessResponse();

        assertEquals("error: MHC\u007F\u001A\u0012", CurrentValuesSingleton.getInstance().get("VIN"));
    }

}
