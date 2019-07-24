package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
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
            mCmd2101.getResponse().process();
            List<String> lines01 = mCmd2101.getResponse().getLines();

            mCmd2102.getResponse().process();
            List<String> lines02 = mCmd2102.getResponse().getLines();

            mCmd2103.getResponse().process();
            List<String> lines03 = mCmd2103.getResponse().getLines();
        } catch (Exception e) {
            int i = 0;
            //
        }
    }
}