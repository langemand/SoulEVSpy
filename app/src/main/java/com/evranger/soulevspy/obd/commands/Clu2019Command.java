package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.Response;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.soulevspy.util.Unit;

import java.util.List;

/**
 * Created by henrik on 13/07/2019.
 */

public class Clu2019Command extends AbstractMultiCommand {
    private BasicCommand mCmd22B002 = null;

    public Clu2019Command() {
        addCommand(new BasicCommand("AT SH 7C6"));
        addCommand(new BasicCommand("AT CRA 7CE"));
        mCmd22B002 = new BasicCommand("22 B0 02");
        addCommand(mCmd22B002);

        mCmd22B002.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7CE(.*)$"));
    }

    public void doProcessResponse() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        try {
            Response r2101 = mCmd22B002.getResponse();
            r2101.process();
            List<String> lines01 = r2101.getLines();
            if (lines01.size() < 3)
                return;

            Response rb002 = mCmd22B002.getResponse();
            try {
                rb002.process();
                List<String> linesb002 = rb002.getLines();
                vals.set(R.string.col_car_odo_km, (double)((rb002.get(1, 5) << 8) | (rb002.get(1, 6))));
            } catch (Exception e) {
                // Ignore
            }


        } catch (Exception e) {
            int i = 0;
            //
        }
    }
}