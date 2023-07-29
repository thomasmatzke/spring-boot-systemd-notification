package de.dm.infrastructure.springbootsystemdnotification.watchdog;

import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;

import de.dm.infrastructure.springbootsystemdnotification.SystemdNotifier;

public class SystemdWatchdogNotificationService {

    private final SystemdNotifier sdNotifyWrapper;

    public SystemdWatchdogNotificationService(final SystemdNotifier sdNotifyWrapper) {
        this.sdNotifyWrapper = sdNotifyWrapper;
    }

    @Scheduled(fixedDelayString = "#{new Integer(${WATCHDOG_USEC}) / 2}", timeUnit = TimeUnit.SECONDS)
    public void notifySystemd() {
        this.sdNotifyWrapper.sendWatchdogKeepalive();
    }

}
