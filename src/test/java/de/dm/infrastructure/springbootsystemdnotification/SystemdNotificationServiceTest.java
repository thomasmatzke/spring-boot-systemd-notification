package de.dm.infrastructure.springbootsystemdnotification;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;

@ExtendWith(MockitoExtension.class)
public class SystemdNotificationServiceTest {

    @Mock
    private SystemdNotificationProperties systemdNotificationProperties;

    @Mock
    private ApplicationReadyEvent applicationReadyEvent;

    @Mock
    private ConfigurableApplicationContext context;

    @Mock
    private SDNotifyWrapper sdNotifyWrapper;

    private SystemdNotificationService systemdNotificationService;

    @BeforeEach
    public void setup() {
        System.clearProperty("NOTIFY_SOCKET");
        Mockito.lenient().when(applicationReadyEvent.getApplicationContext()).thenReturn(context);
        systemdNotificationService = new SystemdNotificationService(systemdNotificationProperties, sdNotifyWrapper);
    }

    @Test
    public void enabledAndSocketEnvVarHasBeenSetBySystemD() {

        when(systemdNotificationProperties.isEnabled()).thenReturn(true);

        System.setProperty("NOTIFY_SOCKET", "something");

        systemdNotificationService.onApplicationEvent(applicationReadyEvent);

        verify(sdNotifyWrapper).sendNotify();
        verify(sdNotifyWrapper).sendStatus("Application Context is ready!");
    }

    @Test
    public void disabled() {

        when(systemdNotificationProperties.isEnabled()).thenReturn(false);

        systemdNotificationService.onApplicationEvent(applicationReadyEvent);

        verifyNoInteractions(sdNotifyWrapper);
    }

    @Test
    public void notifySocketEnvVarHasNotBeenSetBySystemD() {

        when(systemdNotificationProperties.isEnabled()).thenReturn(true);

        systemdNotificationService.onApplicationEvent(applicationReadyEvent);

        verifyNoInteractions(sdNotifyWrapper);
    }
}
