package com.evranger.soulevspy.obd;

import com.evranger.elm327.commands.Command;
import com.evranger.elm327.io.Protocol;
import com.evranger.soulevspy.BaseUnitConversionTest;
import com.evranger.soulevspy.LogFileResponder;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.car_model.ModelSpecificCommands;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.InputStream;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class ProtocolIoniqTest extends BaseUnitConversionTest {

    public ProtocolIoniqTest() {
        super("IoniqEV");
    }

    @Test
    public void testIonicProtocol() throws Exception {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();

        InputStream is = getClass().getClassLoader().getResourceAsStream("ioniq.log.txt") ;
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

        // TODO: assert each of the 153 values...!
        assertEquals(28, vals.get(R.string.col_battery_max_cell_soh_n));
        assertEquals(100.0, vals.get(R.string.col_battery_max_cell_soh_pct));
        assertEquals(87, vals.get(R.string.col_battery_min_cell_soh_n));
        assertEquals(100.0, vals.get(R.string.col_battery_min_cell_soh_pct));
    }
}
