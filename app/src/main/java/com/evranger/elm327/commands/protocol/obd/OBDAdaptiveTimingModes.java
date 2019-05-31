package com.evranger.elm327.commands.protocol.obd;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-26.
 */
public enum OBDAdaptiveTimingModes {

    /**
     * Disabled
     */
    TIMING_OFF('0'),

    /**
     * Adaptive Timing Option 1 (default)
     */
    TIMING_AUTO1('1'),

    /**
     * Adaptive Timing Option 2
     */
    TIMING_AUTO2('2');

    private final char value;

    OBDAdaptiveTimingModes(char value) {
        this.value = value;
    }

    /**
     * <p>Getter for the field <code>value</code>.</p>
     *
     * @return a char.
     */
    public char getValue() {
        return value;
    }
}