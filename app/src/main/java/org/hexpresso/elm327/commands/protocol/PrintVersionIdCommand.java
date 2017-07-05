package org.hexpresso.elm327.commands.protocol;

import org.hexpresso.elm327.commands.AbstractCommand;
import org.hexpresso.soulevspy.obd.values.CurrentValuesSingleton;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class PrintVersionIdCommand extends AbstractCommand {
    private String version;

    public PrintVersionIdCommand() {
        super("AT I");
    }

    public String getVersion() {
        if (version == null) {
            version = getResponse().getLines().get(0);
        }
        return version;
    }
    public void doProcessResponse() {
        CurrentValuesSingleton.getInstance().set("ELM327.VersionId", getVersion());
    }
}
