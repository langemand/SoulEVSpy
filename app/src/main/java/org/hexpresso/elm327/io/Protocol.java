package org.hexpresso.elm327.io;

import org.hexpresso.elm327.commands.Command;
import org.hexpresso.elm327.commands.protocol.RawCommand;
import org.hexpresso.elm327.exceptions.ResponseException;
import org.hexpresso.elm327.exceptions.StoppedException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-27.
 */
public class Protocol {
    //
    private LinkedBlockingQueue<Message> mMessageInputQueue = new LinkedBlockingQueue<>();
    private Thread mExecutionThread = null;

    //
    private LinkedBlockingQueue<Message> mMessageOutputQueue = new LinkedBlockingQueue<>();
    private Thread mProcessingThread = null;
    private List<MessageReceivedListener> mMessageReceivedListeners = new ArrayList<>();

    // Input/output streams
    private InputStream mInputStream = null;
    private OutputStream mOutputStream = null;

    private String mStatus = new String();

    private int mTimeoutCount = 0;

    public Protocol() {

        // Thread used to execute ELM327 commands
        mExecutionThread = new Thread(new Runnable() {
            @Override
            public void run() {
                executeMessages();
            }
        });
        mExecutionThread.setName("BluetoothProtocolExecutionThread");

        // Thread used to process the received messages
        mProcessingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                processReceivedMessages();
            }
        });
        mProcessingThread.setName("BluetoothProtocolProcessingThread");
    }

    /**
     * Adds the specified Command to the message queue and execute it
     * @param command Command to execute
     * @return True if the command was successfully added, false otherwise
     */
    public boolean addCommand(Command command) {
        // Create the message wrapper for the queue
        Message message = new Message(command);

        try {
            mMessageInputQueue.put(message);
        } catch (InterruptedException e) {
            // An error occurred while adding the message to the execution queue, flag the error
            message.setState(Message.State.ERROR_QUEUE);
            addMessageToProcessingQueue(message);
            return false;
        }

        return true;
    }

    public synchronized int numberOfQueuedCommands() {
        return mMessageInputQueue.size();
    }

    public synchronized int numberOfTimeouts() {
        return mTimeoutCount;
    }

    /**
     * Adds the specified Message to the processing queue.
     * @param message Message to add
     */
    private void addMessageToProcessingQueue(Message message) {
        try {
            mMessageOutputQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Command execution thread loop
     * Takes a Message from the message queue and executes it
     */
    private void executeMessages() {
        while (!mExecutionThread.isInterrupted()) {
            Message message = null;

            try {
                message = mMessageInputQueue.take();

                message.setState(Message.State.EXECUTING);

                // TODO PEM : check for errors
                try {
                    message.getCommand().execute(mInputStream, mOutputStream);
                } catch (TimeoutException e) {
                    ++mTimeoutCount;
                    message.setState(Message.State.ERROR_TIMEOUT);
                    continue;
                }

                message.setState(Message.State.FINISHED);
            } catch (InterruptedException e) {
                mExecutionThread.interrupt();
                mStatus = e.getMessage();
                if (mStatus == null) {
                    mStatus = "Interrupted while executing command";
                }
            } catch (IOException e) {
// Ignore?
                // TODO : This is just for a test!
//                final String err = "IOexception: " + e.toString();
//                ((MainActivity)CurrentValuesSingleton.getInstance().getPreferences().getContext()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(CurrentValuesSingleton.getInstance().getPreferences().getContext(), err, Toast.LENGTH_SHORT).show();
//                    }
//                });
                mExecutionThread.interrupt();
                mStatus = e.getMessage();
                if (mStatus == null) {
                    mStatus = "IOException while executing command";
                }

            } catch (StoppedException e) {
                int a = 7; // for having a place to set a breakpoint...
                // Shall this be ignored?
            } catch (ResponseException e) {
//                final String err = "ResponseException: " + e.toString();
//                ((MainActivity)CurrentValuesSingleton.getInstance().getPreferences().getContext()).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(CurrentValuesSingleton.getInstance().getPreferences().getContext(), err, Toast.LENGTH_SHORT).show();
//                    }
//                });
                mExecutionThread.interrupt();
                mStatus = e.getMessage();
                if (mStatus == null) {
                    mStatus = "ResponseException while executing command";
                }
            }

            if(message != null) {
                // Add the Message object to the processing queue
                addMessageToProcessingQueue(message);
            }
        }
        if (mExecutionThread.isInterrupted()) {
            mMessageInputQueue.clear();

            mInputStream = null;
            mOutputStream = null;
        }
    }

    /**
     * Process the received ELM327 messages, then dispatches the messages to the registered
     * MessageReceivedListener objects.
     */
    private void processReceivedMessages() {
        while (!mProcessingThread.isInterrupted())
        {
            Message message = null;

            try {
                message = mMessageOutputQueue.take();
                message.getCommand().doProcessResponse();

                // Notify registered MessageReceivedListener objects
                for (Iterator<MessageReceivedListener> i=mMessageReceivedListeners.iterator(); i.hasNext(); ) {
                    MessageReceivedListener listener = i.next();
                    try {
                        listener.onMessageReceived(message);
                    }
                    catch (RuntimeException e) {
                        // An error occurred, remove the listener
                        i.remove();
                    }
                }
            } catch (InterruptedException e) {
                mProcessingThread.interrupt();
            }
        }
        mMessageOutputQueue.clear();
    }

    /**
     * Starts the protocol
     * @param in Input stream
     * @param out Output stream
     */
    public synchronized void start(InputStream in, OutputStream out) {
        mInputStream = in;
        mOutputStream = out;

        mExecutionThread.start();
        mProcessingThread.start();
    }

    /**
     * Initialize ELM327 device
     */
    public synchronized void init() {
        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand(" ")); // Ensure monitoring is stopped, just in case
        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT I"));
        addCommand(new org.hexpresso.elm327.commands.protocol.ResetAllCommand());
//        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT D")); // Set all to default
//DONT        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT CSM1")); // Silent monitoring not recognized by KW-902
//DONT        addCommand(new org.hexpresso.elm327.commands.protocol.can.CANDisplayDataLengthCodeCommand(false));
        addCommand(new org.hexpresso.elm327.commands.protocol.EchoCommand(false));
        addCommand(new org.hexpresso.elm327.commands.protocol.LinefeedsCommand(false));
        addCommand(new org.hexpresso.elm327.commands.protocol.can.CANSetProtocolCommand(6));
        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT AR"));
        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT AL"));
        addCommand(new org.hexpresso.elm327.commands.protocol.can.CANAutomaticFormattingCommand(true));
        addCommand(new org.hexpresso.elm327.commands.protocol.HeadersCommand(true));
//        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT CEA")); // Try Turn off CAN extended addressing
//        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT CM 00 00 00 00")); // Try Turn off CAN filter mask bits
//        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT FE")); // Try forget events
//        addCommand(new RawCommand("AT KW0")); // Try Don't check Key Words
//        addCommand(new RawCommand("AT IGN")); // Try read ignition state

//DONT        addCommand(new org.hexpresso.elm327.commands.protocol.RawCommand("AT S0")); // NOT SUPPORTED: Decoding needs the space for each hex pair...
        addCommand(new RawCommand("AT ST 80")); // Attempt to fix issue where initial 09 02 returns "NO DATA"
//        addCommand(new RawCommand("AT ST FF")); // Attempt to fix issue where initial 09 02 returns "NO DATA"
    }

    /**
     * Stops the protocol
     */
    public synchronized void stop() {
        // Stops the execution
        mExecutionThread.interrupt();
        mProcessingThread.interrupt();

        // TODO : Cancel the pending messages and transfer to the received queue (flagged as queue error or something)
//        mMessageInputQueue.clear();
//        mMessageOutputQueue.clear();
//
//        mInputStream = null;
//        mOutputStream = null;
    }

    public synchronized String setStatus(String newStatus) {
        String ret = new String(mStatus);
        mStatus = newStatus;
        return ret;
    }

    /**
     * A received Message listener.
     */
    public interface MessageReceivedListener {

        /**
         * Receives notification that an ELM327 message response has been received.
         * @param message The Message object.
         */
        void onMessageReceived(Message message);
    }

    /**
     * Registers an event listener that listens for received ELM327 Message.
     * @param listener A MessageReceivedListener object
     * @return True if the listener was successfully registered, false otherwise.
     */
    public boolean registerOnMessageReceivedListener(MessageReceivedListener listener) {
        return mMessageReceivedListeners.add(listener);
    }

    /**
     * Unregisters an event listener.
     * @param listener A MessageReceivedListener object
     * @return True if the listener was successfully unregistered, false otherwise.
     */
    public boolean unregisterOnMessageReceivedListener(MessageReceivedListener listener) {
        return mMessageReceivedListeners.remove(listener);
    }
}