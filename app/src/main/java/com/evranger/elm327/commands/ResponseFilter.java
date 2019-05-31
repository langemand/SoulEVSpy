package com.evranger.elm327.commands;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public interface ResponseFilter {
    /**
     *
     * @param response
     */
    void onResponseReceived(Response response);
}