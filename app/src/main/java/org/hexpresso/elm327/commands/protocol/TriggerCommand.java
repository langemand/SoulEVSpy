package org.hexpresso.elm327.commands.protocol;

import org.hexpresso.elm327.commands.AbstractCommand;

/**
 * Created by henrik on 14/06/2017.
 */

public class TriggerCommand extends AbstractCommand {
    public interface Callback {
        void call();
    }
    Callback mCallback;
    public TriggerCommand(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void doProcessResponse() {
        mCallback.call();
    };

}
