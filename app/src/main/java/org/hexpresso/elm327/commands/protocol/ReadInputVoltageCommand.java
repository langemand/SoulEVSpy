package org.hexpresso.elm327.commands.protocol;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.elm327.commands.filters.RegularExpressionResponseFilter;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-26.
 */
public class ReadInputVoltageCommand extends AbstractCommand {

    private Double voltage;

    public ReadInputVoltageCommand() {
        super("AT RV");
        addResponseFilter(new RegularExpressionResponseFilter("(\\d+\\.?\\d+)V?"));
        withAutoProcessResponse(true);
    }

    public double getInputVoltage() {
        voltage = new Double(getResponse().getLines().get(0));
        return voltage;
    }
    public void doProcessResponse() {
        CurrentValuesSingleton.getInstance().set("ELM327.Voltage_V", new Double(getInputVoltage()));
    }
}
