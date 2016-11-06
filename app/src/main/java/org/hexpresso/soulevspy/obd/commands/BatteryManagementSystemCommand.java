package org.hexpresso.soulevspy.obd.commands;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.elm327.commands.filters.RemoveSpacesResponseFilter;

/**
 * Battery Management System command for the Soul EV
 * Note : This command assumes that headers are active since 2 ECUs answer to those commands!
 *
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-22.
 */
public class BatteryManagementSystemCommand extends AbstractCommand {

    private Double stateOfCharge = null;

    /**
     * Constructor
     */
    public BatteryManagementSystemCommand() {
        super("21 01");

        // Only keep messages from 7EC ECU
        addResponseFilter(new RegularExpressionResponseFilter("^7EC(.*)$"));
        addResponseFilter(new RemoveSpacesResponseFilter());
    }

    /**
     * State of Charge (%)
     */
    public double getStateOfCharge() {
//        if (stateOfCharge == null) {
            stateOfCharge = getResponse().get(1, 1) * 0.5;
//        }
        return stateOfCharge;
    }
}