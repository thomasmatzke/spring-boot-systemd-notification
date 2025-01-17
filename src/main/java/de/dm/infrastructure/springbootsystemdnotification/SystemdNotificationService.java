package de.dm.infrastructure.springbootsystemdnotification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

public class SystemdNotificationService implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(SystemdNotificationService.class);

    private static final String NOTIFY_SOCKET = "NOTIFY_SOCKET";

    private final SystemdNotificationProperties systemdNotificationProperties;

    private final SystemdNotifier systemdNotifier;

    public SystemdNotificationService(final SystemdNotificationProperties systemdNotificationProperties, SystemdNotifier systemdNotifier) {
        this.systemdNotificationProperties = systemdNotificationProperties;
        this.systemdNotifier = systemdNotifier;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {

        if (this.systemdNotificationProperties.isEnabled()) {

            final ConfigurableApplicationContext context = applicationReadyEvent.getApplicationContext();

            final String socketName = System.getenv(NOTIFY_SOCKET) != null ? System.getenv(NOTIFY_SOCKET) : System.getProperty(NOTIFY_SOCKET);

            if (socketName != null) {

                LOG.info("Notifying systemd that application context ({}) is ready! NOTIFY_SOCKET: {}", context.getId(), socketName);
                this.systemdNotifier.sendMainPID();
                this.systemdNotifier.sendNotify();
                this.systemdNotifier.sendStatus("Application Context is ready!");
            } else {
                LOG.warn("systemd Notification enabled, but systemd not present (systemd hasn't set env variable 'NOTIFY_SOCKET')");
            }
        }
    }

}
