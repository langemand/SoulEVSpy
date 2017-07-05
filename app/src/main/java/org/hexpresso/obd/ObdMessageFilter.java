package org.hexpresso.obd;

import java.util.ArrayList;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-10.
 */
public abstract class ObdMessageFilter {

    private String mMessageIdentifier   = null;
    private ArrayList<ObdMessageFilterListener> mObdMessageFilterListeners = null;

    private ObdMessageFilter() {
    }

    protected ObdMessageFilter(String messageIdentifier) {
        mObdMessageFilterListeners = new ArrayList<>();
        mMessageIdentifier = messageIdentifier;
    }

    public String messageIdentifier() {
        return mMessageIdentifier;
    }

    public void receive(String rawData)
    {
        // Check that the message identifier match
        if ( !rawData.startsWith( mMessageIdentifier ) )
        {
            return;
        }

        ObdMessageData obdMessage = new ObdMessageData(rawData);

        if ( doProcessMessage(obdMessage) )
        {
            for (ObdMessageFilterListener listener: mObdMessageFilterListeners) {
                listener.onMessageReceived(this);
            }
        }
    }

    protected abstract boolean doProcessMessage(ObdMessageData messageData);


    public interface ObdMessageFilterListener {
        void onMessageReceived(ObdMessageFilter messageFilter);
    }

    public void addObdMessageFilterListener(ObdMessageFilterListener listener) {
        mObdMessageFilterListeners.add(listener);
    }
}
