package com.evranger.soulevspy.obd.commands;

import com.evranger.elm327.commands.AbstractMultiCommand;
import com.evranger.soulevspy.obd.values.CurrentValuesSingleton;
import com.evranger.elm327.commands.filters.RegularExpressionResponseFilter;
import com.evranger.obd.ObdMessageData;
import com.evranger.soulevspy.R;

import java.util.List;

/**
 * Created by henrik on 08/05/2019.
 */

public class OnBoardChargerCommand extends AbstractMultiCommand {
    private BasicCommand mCmd2102 = null;

    public OnBoardChargerCommand() {
        addCommand(new BasicCommand("AT SH 7DF"));
        addCommand(new BasicCommand("AT CRA 79C"));
        mCmd2102 = new BasicCommand("21 02");
        addCommand(mCmd2102);

        mCmd2102.addResponseFilter(new RegularExpressionResponseFilter("^79C(.*)$"));
//        mCmd2101.addResponseFilter(new RemoveSpacesResponseFilter());
    }

    public void doProcessResponse() {
        CurrentValuesSingleton vals = CurrentValuesSingleton.getInstance();
        try {
            mCmd2102.getResponse().process();
            List<String> lines = mCmd2102.getResponse().getLines();
            if (lines.size() != 4)
                return;

            ObdMessageData obdData1 = new ObdMessageData(lines.get(1));

            double acInV = (obdData1.getDataByte(3)*256+obdData1.getDataByte(4)) / 10.0;
            vals.set(R.string.col_obc_ac_in_V, acInV);

            ObdMessageData obdData2 = new ObdMessageData(lines.get(2));

            boolean isEarlyVersion = (obdData2.getDataByte(4) == 0 && obdData2.getDataByte(5) == 0);
            double dcOutV;
            double acInA;
            if (isEarlyVersion) {
                dcOutV = (obdData1.getDataByte(7)*256 + obdData2.getDataByte(1)) / 10.0;
                acInA = (obdData2.getDataByte(2)*256 + obdData2.getDataByte(3)) / 10.0;
            } else {
                dcOutV = (obdData2.getDataByte(2)*256 + obdData2.getDataByte(3)) / 10.0;
                acInA = (obdData2.getDataByte(4)*256 + obdData2.getDataByte(5)) / 10.0;
            }
            vals.set(R.string.col_obc_dc_out_V, dcOutV);
            vals.set(R.string.col_obc_ac_in_A, acInA);

            int duty_cycle = obdData2.getDataByte(7);
            vals.set(R.string.col_obc_pilot_duty_cycle, Double.valueOf(duty_cycle/3.0));

            ObdMessageData obdData3 = new ObdMessageData(lines.get(3));

            int temp1 = obdData3.getDataByte(1);
            vals.set(R.string.col_obc_temp_1_C, temp1);

            int temp2 = obdData3.getDataByte(2);
            vals.set(R.string.col_obc_temp_2_C, temp2);

            int temp3 = obdData3.getDataByte(3);
            vals.set(R.string.col_obc_temp_3_C, temp3);
        } catch (Exception e) {
            //
        }
    }
}