package de.dm.infrastructure.springbootsystemdnotification;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@EnableConfigurationProperties(SystemdNotificationProperties.class)
public class SystemdNotificationAutoconfiguration {

   public static final Logger LOGGER = LoggerFactory.getLogger(SystemdNotificationAutoconfiguration.class);

   @Value("${WATCHDOG_USEC}")
   private String watchdogUsecString;

   @ConditionalOnMissingBean
   @Bean
   SystemdNotifier systemdNotifier() {
      return new SDNotifyWrapper();
   }

   @ConditionalOnProperty(name = "systemd.notification.enabled")
   @ConditionalOnMissingBean
   @Bean
   SystemdNotificationService systemdNotificationService(
         final SystemdNotificationProperties systemdNotificationProperties) {
      return new SystemdNotificationService(systemdNotificationProperties, systemdNotifier());
   }

   @Bean(destroyMethod = "shutdownNow")
   @Qualifier("watchdogNotifierPool")
   public ScheduledExecutorService watchdogNotifierPool() {
      return Executors.newScheduledThreadPool(1);
   }

   /**
    * Contrary to the documentation, the WATCHDOG_USEC environment variable is not
    * always unset if no timeout was set in the systemd service file.
    * Under a WSL environment with no defined WatchdogSec property, the
    * WATCHDOG_USEC environment the WATCHDOG_USEC variable was 10.
    * To mitigate this behavior we only start watchdog notifications, if
    * WATCHDOG_USEC >= 1000000.
    */
   @EventListener(value = ApplicationReadyEvent.class, condition = "@environment.containsProperty('systemd.notification.enabled') && @environment.containsProperty('WATCHDOG_USEC') && @environment.getProperty('WATCHDOG_USEC', T(java.lang.Integer)) > 0")
   public void startWatchdogTask() {
      LOGGER.debug("WATCHDOG_USEC={}", this.watchdogUsecString);
      var watchdogUsecInt = Integer.parseInt(watchdogUsecString);
      if (watchdogUsecInt < 1000000) {
         LOGGER.warn("WATCHDOG_USEC < 1000000! Not starting systemd notifications as a workaround for undocumented behavior.");
         return;
      }
      int delaySeconds = watchdogUsecInt / 2;
      LOGGER.info("Started systemd watchdog notification. Interval: {} seconds", delaySeconds / 1000000);
      watchdogNotifierPool().scheduleWithFixedDelay(this.systemdNotifier()::sendWatchdogKeepalive, 0, delaySeconds,
            TimeUnit.MICROSECONDS);
   }

}
