package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.Response;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.List;

/**
 * Created by henrik on 16/12/2017.
 */

public class TPMS2019Command extends AbstractMultiCommand {
    private BasicCommand mCmd22C00B = null;
    private BasicCommand mCmd22C002 = null;

    public TPMS2019Command() {
        mCmd22C002 = new BasicCommand("22 C0 02");
        mCmd22C00B = new BasicCommand("22 C0 0B");
        addCommand(new BasicCommand("AT SH 7A0"));
        addCommand(mCmd22C002);
        addCommand(mCmd22C00B);

        mCmd22C002.addResponseFilter(new RegularExpressionResponseFilter("^7A8(.*)$"));
        mCmd22C00B.addResponseFilter(new RegularExpressionResponseFilter("^7A8(.*)$"));

        //withAutoProcessResponse(true);
    }

    @Override
    public void doProcessResponse() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();

        mCmd22C002.getResponse().process();
        List<String> lines = mCmd22C002.getResponse().getLines();
        if (lines.size() < 0)
            return;

        // TODO

        mCmd22C00B.getResponse().process();
        List<String> lines0B = mCmd22C00B.getResponse().getLines();
        if (lines0B.size() < 4)
            return;

        Response r = mCmd22C00B.getResponse();

        vals.set(R.string.col_tire_pressure_psi, 1, "_psi", Double.valueOf(r.get(1, 2) * 0.2));
        vals.set(R.string.col_tire_temperature_C, 1, "_C", Integer.valueOf(r.get(1,3) - 50));

        vals.set(R.string.col_tire_pressure_psi, 2, "_psi", Double.valueOf(r.get(1, 6) * 0.2));
        vals.set(R.string.col_tire_temperature_C, 2, "_C", Integer.valueOf(r.get(1,7) - 50));

        vals.set(R.string.col_tire_pressure_psi, 3, "_psi", Double.valueOf(r.get(2, 3) * 0.2));
        vals.set(R.string.col_tire_temperature_C, 3, "_C", Integer.valueOf(r.get(2,4) - 50));

        vals.set(R.string.col_tire_pressure_psi, 4, "_psi", Double.valueOf(r.get(2, 7) * 0.2));
        vals.set(R.string.col_tire_temperature_C, 4, "_C", Integer.valueOf(r.get(3,1) - 50));
    }
}
