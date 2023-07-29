package de.dm.infrastructure.springbootsystemdnotification;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import de.dm.infrastructure.springbootsystemdnotification.watchdog.SystemdWatchdogNotificationService;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(SystemdNotificationProperties.class)
public class SystemdNotificationAutoconfiguration {

    @ConditionalOnProperty(name = "systemd.notification.enabled")
    @ConditionalOnMissingBean
    @Bean
    SystemdNotificationService systemdNotificationService(final SystemdNotificationProperties systemdNotificationProperties) {
        return new SystemdNotificationService(systemdNotificationProperties);
    }

    @ConditionalOnProperty(name = {"systemd.watchdog.notification.enabled", "WATCHDOG_USEC"})
    @ConditionalOnMissingBean
    @Bean
    SystemdWatchdogNotificationService systemdWatchdogNotificationService() {
        return new SystemdWatchdogNotificationService(new SDNotifyWrapper());
    }
}
