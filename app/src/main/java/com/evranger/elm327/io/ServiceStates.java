package com.evranger.elm327.io;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-11-02.
 */
public enum ServiceStates {
    /**
     * The service is in connecting state
     */
    STATE_CONNECTING,

    /**
     * The service is in connected state
     */
    STATE_CONNECTED,

    /**
     * The service is in disconnecting state
     */
    STATE_DISCONNECTING,

    /**
     * The service is in disconnected state
     */
    STATE_DISCONNECTED
}
