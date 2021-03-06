/*
 * Copyright 2017 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.launch;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.debug.core.ILaunch;
import org.rf.ide.core.execution.server.DefaultAgentServerStatusListener;


public class ProcessConnectingInRunServerListener extends DefaultAgentServerStatusListener {

    protected final ILaunch launch;

    public ProcessConnectingInRunServerListener(final ILaunch launch) {
        this.launch = launch;
    }

    @Override
    public void clientConnected(final int clientId) {
        Optional<IRobotProcess> process = getProcess();
        while (!process.isPresent()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                // retry
            }
            process = getProcess();
        }
        process.get().setConnectedToTests(true);
    }

    private Optional<IRobotProcess> getProcess() {
        return Arrays.stream(launch.getProcesses())
                .filter(IRobotProcess.class::isInstance)
                .map(IRobotProcess.class::cast)
                .findFirst();
    }
}
