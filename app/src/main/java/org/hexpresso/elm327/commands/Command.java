package org.hexpresso.elm327.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * The Command interface represents a command to be executed on an ELM327 device.
 * <p/>
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-24.
 */
public interface Command {

    void execute(InputStream in, OutputStream out) throws IOException, InterruptedException, TimeoutException;

    Response getResponse();

    void doProcessResponse();
}
