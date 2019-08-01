package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.List;

/**
 * Created by henrik on 13/07/2019.
 */

public class Aircon2019Command extends AbstractMultiCommand {
    private BasicCommand mCmd2101 = null;
    private BasicCommand mCmd2102 = null;

    public Aircon2019Command() {
        addCommand(new BasicCommand("AT SH 7B3"));
//        addCommand(new BasicCommand("AT CRA 7BB"));
        mCmd2101 = new BasicCommand("22 01 00");
        mCmd2102 = new BasicCommand("22 01 02");
        addCommand(mCmd2101);
        addCommand(mCmd2102);

        mCmd2101.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7BB(.*)$"));
        mCmd2102.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7BB(.*)$"));
    }

    public void doProcessResponse() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        try {
            mCmd2101.getResponse().process();
            List<String> lines01 = mCmd2101.getResponse().getLines();

            mCmd2102.getResponse().process();
            List<String> lines02 = mCmd2102.getResponse().getLines();
        } catch (Exception e) {
            int i = 0;
            //
        }
    }
}