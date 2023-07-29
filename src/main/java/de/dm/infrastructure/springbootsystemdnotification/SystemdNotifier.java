package de.dm.infrastructure.springbootsystemdnotification;

public interface SystemdNotifier {

    void sendMainPID();

    void sendNotify();

    void sendStatus(String status);

    void sendWatchdogKeepalive();

}
