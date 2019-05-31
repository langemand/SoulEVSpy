package com.evranger.soulevspy.io;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-06.
 */
public class ELM327Protocol {

    private final static String init[] = { "AT Z",      // Reset all
                                           "AT I",      // Print the version ID
                                           "AT E0",     // Echo off
                                           "AT L0",     // Linefeeds off
                                           "AT SP 6",   // Select protocol to ISO 15765-4 CAN (11 bit ID, 500 kbit/s)
                                           "AT AL",     // Allow Long (>7 byte) messages
                                           "AT AR",     // Automatically receive
                                           "AT H1",     // Headers on (debug only)
                                           "AT S1",     // Printing of spaces on
                                           "AT D1",     // Display of the DLC on
                                           "AT CAF0",   // Automatic formatting off
                                          };

    private final static String getVIN  = "0902";
    private final static String getPIDs = "0100";

    /*
    49 02 01 00 00 00 31
    49 02 01 00 00 00 31
    49 02 01 00 00 00 31
    49 02 01 00 00 00 31
     */
}