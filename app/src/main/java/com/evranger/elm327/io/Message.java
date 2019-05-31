package com.evranger.elm327.io;

import com.evranger.elm327.commands.Command;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-28.
 */
public class Message {

    public enum State {
        READY,
        EXECUTING,
        FINISHED,

        // Errors
        ERROR_EXECUTION,
        ERROR_NOT_SUPPORTED,
        ERROR_QUEUE,
        ERROR_TIMEOUT
    }

    private static AtomicLong messageIdentifier = new AtomicLong(0L);

    private final Command mCommand;
    private final long  mIdentifier;
    private State mState;

    Message(Command command) {
        mCommand = command;
        mIdentifier = messageIdentifier.incrementAndGet();
        mState = State.READY;
    }

    private Message() {
        this(null);
    }

    public Command getCommand() {
        return mCommand;
    }

    public long getIdentifier() {
        return mIdentifier;
    }

    public State getState() {
        return mState;
    }

    void setState(State state) {
        mState = state;
    }
}