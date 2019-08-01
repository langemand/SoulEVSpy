package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.elm327.commands.Response;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.R;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;

import java.util.List;

/**
 * Created by henrik on 13/07/2019.
 */

public class Mcu2019Command extends AbstractMultiCommand {
    private BasicCommand mCmd2101 = null;
    private BasicCommand mCmd2102 = null;
    private BasicCommand mCmd2103 = null;
    private BasicCommand mCmd2104 = null;
    private BasicCommand mCmd2105 = null;
    private BasicCommand mCmd2106 = null;

    public Mcu2019Command() {
        addCommand(new BasicCommand("AT SH 7E3"));
//        addCommand(new BasicCommand("AT CRA 7EB"));
        mCmd2101 = new BasicCommand("21 01");
        mCmd2102 = new BasicCommand("21 02");
        mCmd2103 = new BasicCommand("21 03");
        mCmd2104 = new BasicCommand("21 04");
        mCmd2105 = new BasicCommand("21 05");
        mCmd2106 = new BasicCommand("21 06");
        addCommand(mCmd2101);
        addCommand(mCmd2102);
        addCommand(mCmd2103);
        addCommand(mCmd2104);
        addCommand(mCmd2105);
        addCommand(mCmd2106);

        mCmd2101.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EB(.*)$"));
        mCmd2102.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EB(.*)$"));
        mCmd2103.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EB(.*)$"));
        mCmd2104.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EB(.*)$"));
        mCmd2105.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EB(.*)$"));
        mCmd2106.addResponseFilter(new RegularExpressionResponseFilter("^\\s*7EB(.*)$"));
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

            mCmd2104.getResponse().process();
            List<String> lines04 = mCmd2104.getResponse().getLines();

            mCmd2105.getResponse().process();
            List<String> lines05 = mCmd2105.getResponse().getLines();

            mCmd2106.getResponse().process();
            List<String> lines06 = mCmd2106.getResponse().getLines();
        } catch (Exception e) {
            int i = 0;
            //
        }
    }
}