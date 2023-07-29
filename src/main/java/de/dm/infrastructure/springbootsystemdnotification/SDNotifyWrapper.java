package de.dm.infrastructure.springbootsystemdnotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.faljse.SDNotify.SDNotify;

public class SDNotifyWrapper implements SystemdNotifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(SDNotifyWrapper.class);

    @Override
    public void sendMainPID() {
        final int pid = Math.toIntExact(ProcessHandle.current().pid());
        LOGGER.debug("Sending main PID to systemd: {}..." + pid);
        SDNotify.sendMainPID(pid);
    }

    @Override
    public void sendNotify() {
        LOGGER.debug("Notifying systemd...");
        SDNotify.sendNotify();
    }

    @Override
    public void sendStatus(final String status) {
        LOGGER.debug("Sending status to systemd: {}...", status);
        SDNotify.sendStatus(status);
    }

    @Override
    public void sendWatchdogKeepalive() {
        LOGGER.debug("Sending watchdog keepalive message to systemd: {}...");
        SDNotify.sendWatchdog();
    }

}
