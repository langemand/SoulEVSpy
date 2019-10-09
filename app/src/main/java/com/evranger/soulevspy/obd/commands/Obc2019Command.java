package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.Response;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.List;

/**
 * Created by henrik on 13/07/2019.
 */

public class Obc2019Command extends AbstractMultiCommand {
    private BasicCommand mCmd2101 = null;
    private BasicCommand mCmd2102 = null;
    private BasicCommand mCmd2103 = null;

    public Obc2019Command() {
        addCommand(new BasicCommand("AT SH 7E5"));
        addCommand(new BasicCommand("AT CRA 7ED"));
        mCmd2101 = new BasicCommand("21 01");
        mCmd2102 = new BasicCommand("21 02");
        mCmd2103 = new BasicCommand("21 03");
        addCommand(mCmd2101);
        addCommand(mCmd2102);
        addCommand(mCmd2103);

        mCmd2101.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7ED(.*)$"));
        mCmd2102.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7ED(.*)$"));
        mCmd2103.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7ED(.*)$"));
    }

    public void doProcessResponse() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        try {
            Response r2101 = mCmd2101.getResponse();
            r2101.process();
            List<String> lines01 = r2101.getLines();
            if (lines01.size() < 8)
                return;

            Response r2102 = mCmd2102.getResponse();
            r2102.process();
            List<String> lines02 = r2102.getLines();
            if (lines02.size() < 5)
                return;

            Response r2103 = mCmd2103.getResponse();
            r2103.process();
            List<String> lines03 = r2103.getLines();
            if (lines01.size() < 8)
                return;

            double pilotDutyCyclePct = ((r2101.get(4, 3)<<8) | r2101.get(4, 4)) / 10.0;
            double temp2C = r2101.get(2, 3) / 2 - 40;
            double tempC = r2101.get(6, 4) / 2 - 40;
            double acInV = r2101.get(6, 7);
            double dcOutV = ((r2101.get(7, 4)<<8) | r2101.get(7, 5)) / 10.0;
            double acInA = ((r2103.get(1,1)<<8) | r2103.get(1,2)) / 100.0;

            vals.set(R.string.col_obc_pilot_duty_cycle, pilotDutyCyclePct);
            vals.set(R.string.col_obc_temp_1_C, tempC);
            vals.set(R.string.col_obc_temp_2_C, temp2C);
            vals.set(R.string.col_obc_ac_in_V, acInV);
            vals.set(R.string.col_obc_dc_out_V, dcOutV);
            vals.set(R.string.col_obc_ac_in_A, acInA);
        } catch (Exception e) {
            int i = 0;
            //
        }
    }
}