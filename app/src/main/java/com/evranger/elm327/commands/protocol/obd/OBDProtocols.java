package com.evranger.elm327.commands.protocol.obd;

/**
 * All OBD protocols supported by the ELM327.
 *
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-26.
 */
public enum OBDProtocols {

    /**
     * Automatic
     */
    AUTO('0'),

    /**
     * SAE J1850 PWM (41.6 kbaud)
     */
    SAE_J1850_PWM('1'),

    /**
     * SAE J8150 VPW (10.4 kbaud)
     */
    SAE_J1850_VPW('2'),

    /**
     * ISO 9141-2 (5 baud init, 10.4 kbaud)
     */
    ISO_9141_2('3'),

    /**
     * ISO 14230-4 KWP (5 baud init, 10.4 kbit/s)
     */
    ISO_14230_4_KWP('4'),

    /**
     * ISO 14230-4 KWP (fast init, 10.4 kbit/s)
     */
    ISO_14230_4_KWP_FAST('5'),

    /**
     * ISO 15765-4 CAN (11 bit ID, 500 kbit/s)
     */
    ISO_15765_4_CAN('6'),

    /**
     * ISO 15765-4 CAN (29 bit ID, 500 kbit/s)
     */
    ISO_15765_4_CAN_B('7'),

    /**
     * ISO 15765-4 CAN (11 bit ID, 250 kbit/s)
     */
    ISO_15765_4_CAN_C('8'),

    /**
     * ISO 15765-4 CAN (29 bit ID, 250 kbit/s)
     */
    ISO_15765_4_CAN_D('9'),

    /**
     * SAE J939 CAN (29 bit ID, 250 kbaud [user adjustable])
     */
    SAE_J1939_CAN('A'),

    /**
     * USER1 CAN (11 bit ID [user adjustable], 125 kbaud [user adjustable])
     */
    USER1_CAN('B'),

    /**
     * USER2 CAN (11 bit ID [user adjustable], 50 kbaud [user adjustable])
     */
    USER2_CAN('C');

    private final char value;

    OBDProtocols(char value) {
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