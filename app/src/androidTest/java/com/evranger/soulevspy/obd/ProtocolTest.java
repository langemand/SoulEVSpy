package com.evranger.soulevspy.obd;

import com.evranger.elm327.commands.Command;
import com.evranger.elm327.io.Protocol;
import com.evranger.soulevspy.BaseUnitConversionTest;
import com.evranger.soulevspy.LogFileResponder;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.car_model.ModelSpecificCommands;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.TestCase.assertEquals;


@RunWith(MockitoJUnitRunner.class)
public class ProtocolTest extends BaseUnitConversionTest {
    public ProtocolTest() {
        super("SoulEV2015");
    }

    @Test
    public void testProtocol() throws Exception {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();

        InputStream is = getClass().getClassLoader().getResourceAsStream("testLogFileResponder.log.txt") ;
        LogFileResponder lfr = new LogFileResponder(is);

        Protocol protocol = new Protocol();
        protocol.start(lfr.getInput(), lfr.getOutput());
        protocol.init();

        ModelSpecificCommands model = new ModelSpecificCommands(vals.getPreferences());
        List commands = model.getLoopCommands();
        Command lastCommand = null;
        for (Object command : commands) {
            lastCommand = (Command)command;
            protocol.addCommand(lastCommand);
        }

// Await end of command processing
        while (vals.get("system.scan_end_time_ms") == null) {
            Thread.sleep(100);
        }

        assertEquals("", lfr.getMessages());

        assertEquals(19.5, vals.get(R.string.col_car_ambient_C));
        assertEquals(20715.8, vals.get(R.string.col_car_odo_km));
        // TODO: assert each of the 190 values...!
    }
}
