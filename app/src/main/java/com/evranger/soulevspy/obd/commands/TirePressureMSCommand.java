package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.List;

/**
 * Created by henrik on 16/12/2017.
 */

public class TirePressureMSCommand extends AbstractMultiCommand {
    private BasicCommand mCmd2106 = null;

    public TirePressureMSCommand() {
        mCmd2106 = new BasicCommand("21 06");
        addCommand(new BasicCommand("AT SH 7DF")); //"AT SH 7E4"));
        addCommand(new BasicCommand("AT CRA 7DE"));
        addCommand(mCmd2106);

        mCmd2106.addResponseFilter(new RegularExpressionResponseFilter("^7DE(.*)$"));

        //withAutoProcessResponse(true);
    }

    @Override
    public void doProcessResponse() {
        mCmd2106.getResponse().process();
        List<String> lines = mCmd2106.getResponse().getLines();
        if (lines.size() < 5)
            return;

        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();

        for (int i = 1; i < 5; ++i) {
            ObdMessageData obdData = new ObdMessageData(lines.get(i));
            vals.set(R.string.col_tire_pressure, i, "_psi", Double.valueOf(obdData.getDataByte(i) * 0.25));
        }

        for (int i = 1; i < 5; ++i) {
            ObdMessageData obdData = new ObdMessageData(lines.get(i));
            vals.set(R.string.col_tire_temperature, i, "_C", Integer.valueOf(obdData.getDataByte(i+1) - 55));
        }
    }
}
