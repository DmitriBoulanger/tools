package de.dbo.logging;

import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.restoreOriginalSystemOutputs;
import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.sendSystemOutAndErrToSLF4J;
import static uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J.systemOutputsAreSLF4JPrintStreams;

public final class SysoutInitialization {
    private static final Object  LOCK      = new Object();

    private static SysoutInitialization singleton = null;
    private static final String         ID        = "System.out and System.err to logger ";

    public static SysoutInitialization instance() {
        synchronized (LOCK) {
            if (null == singleton) {
                singleton = new SysoutInitialization();
            }
            return singleton;
        }
    }

    private SysoutInitialization() {
        // never initialize from outside
    }

    private boolean done = false;

    /**
     * all console-messages send to the relevant logger.
     * The operation is only done if the root-logger has been already initialized
     *
     * @throws Exception
     */
    public final void outAndErrToLog(boolean yes) throws Exception {
        synchronized (LOCK) {
            // should be done only once!
            if (done) {
                System.out.println(ID + "REJECTED");
                return;
            }
            else {
                done = true;
            }
        }

        if (yes && !systemOutputsAreSLF4JPrintStreams()) {
            System.out.println(ID + " ....");
            try {
                sendSystemOutAndErrToSLF4J();
            }
            catch(Throwable e) {
                System.err.println(ID + "ERROR: " + e);
            }
            System.out.println(ID + " DONE");
            return;
        }
        if (!yes && systemOutputsAreSLF4JPrintStreams()) {
            restoreOriginalSystemOutputs();
        }
    }


}
