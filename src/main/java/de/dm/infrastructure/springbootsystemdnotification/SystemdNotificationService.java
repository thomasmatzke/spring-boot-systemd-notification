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

    private final SystemdNotifier sdNotifyWrapper;

    public SystemdNotificationService(final SystemdNotificationProperties systemdNotificationProperties) {
        this.systemdNotificationProperties = systemdNotificationProperties;
        this.sdNotifyWrapper = new SDNotifyWrapper();
    }

    public SystemdNotificationService(final SystemdNotificationProperties systemdNotificationProperties, final SystemdNotifier sdNotifyWrapper) {
        this.systemdNotificationProperties = systemdNotificationProperties;
        this.sdNotifyWrapper = sdNotifyWrapper;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent applicationReadyEvent) {

        if (this.systemdNotificationProperties.isEnabled()) {

            final ConfigurableApplicationContext context = applicationReadyEvent.getApplicationContext();

            final String socketName = System.getenv(NOTIFY_SOCKET) != null ? System.getenv(NOTIFY_SOCKET) : System.getProperty(NOTIFY_SOCKET);

            if (socketName != null) {

                LOG.info("Notifying systemd that application context ({}) is ready! NOTIFY_SOCKET: {}", context.getId(), socketName);
                this.sdNotifyWrapper.sendMainPID();
                this.sdNotifyWrapper.sendNotify();
                this.sdNotifyWrapper.sendStatus("Application Context is ready!");
            } else {
                LOG.warn("systemd Notification enabled, but systemd not present (systemd hasn't set env variable 'NOTIFY_SOCKET')");
            }
        }
    }

}
