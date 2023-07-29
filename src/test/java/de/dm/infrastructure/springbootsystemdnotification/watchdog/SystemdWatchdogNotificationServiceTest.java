package de.dm.infrastructure.springbootsystemdnotification.watchdog;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.dm.infrastructure.springbootsystemdnotification.SystemdNotifier;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"WATCHDOG_USEC=2"})
class SystemdWatchdogNotificationServiceTest {

    @EnableScheduling
    @Configuration
    static class TestConfiguration {

        @Bean
        TestSystemdNotifier testSystemdNotifier() {
            return new TestSystemdNotifier();
        }

        @Bean
        SystemdWatchdogNotificationService systemdWatchdogNotificationService(final TestSystemdNotifier testSystemdNotifier) {
            return new SystemdWatchdogNotificationService(testSystemdNotifier);
        }
    }

    @Autowired
    SystemdWatchdogNotificationService watchdogNotificationService;

    @Autowired
    TestSystemdNotifier testSystemdNotifier;

    @Test
    void testScheduling() throws InterruptedException {
        Awaitility.await().atMost(Duration.ofSeconds(2)).untilAtomic(this.testSystemdNotifier.counter, Matchers.greaterThan(0));
    }

    private static class TestSystemdNotifier implements SystemdNotifier {

        AtomicInteger counter = new AtomicInteger(0);

        @Override
        public void sendMainPID() {
            // nop
        }

        @Override
        public void sendNotify() {
            // nop
        }

        @Override
        public void sendStatus(final String status) {
            // nop
        }

        @Override
        public void sendWatchdogKeepalive() {
            this.counter.incrementAndGet();
        }

    }

}
