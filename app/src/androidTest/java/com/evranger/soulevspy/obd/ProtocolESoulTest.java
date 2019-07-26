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
public class ProtocolESoulTest extends BaseUnitConversionTest {

    public ProtocolESoulTest() {
        super("eSoul2020");
    }

    @Test
    public void testESoul2020Protocol() throws Exception {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();

        InputStream is = getClass().getClassLoader().getResourceAsStream("eSoul2020.log.txt") ;
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

        // Assert each of the values...!
// BMC (TPMS):
        assertEquals(1337491198, Math.round((double)vals.get("car.odo_km")*100));
        assertEquals(386, Math.round((double)vals.get("tire.pressure1_psi")*10));
        assertEquals(384, Math.round((double)vals.get("tire.pressure2_psi")*10));
        assertEquals(388, Math.round((double)vals.get("tire.pressure3_psi")*10));
        assertEquals(400, Math.round((double)vals.get("tire.pressure4_psi")*10));
        assertEquals(22, vals.get("tire.temperature1_C"));
        assertEquals(22, vals.get("tire.temperature2_C"));
        assertEquals(21, vals.get("tire.temperature3_C"));
        assertEquals(21, vals.get("tire.temperature4_C"));

// TODO MCU:

// TODO OBC:
        assertEquals(0.0, vals.get(R.string.col_obc_pilot_duty_cycle));
        assertEquals(22.0, vals.get(R.string.col_obc_temp_1_C));
        assertEquals(0.0, vals.get(R.string.col_obc_ac_in_V));
        assertEquals(357.9, vals.get(R.string.col_obc_dc_out_V));
        assertEquals(0.0, vals.get(R.string.col_obc_ac_in_A));

        // TODO Aircon:


    }
}
