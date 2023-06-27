package io.debezium.examples.notification;

import io.debezium.config.CommonConnectorConfig;
import io.debezium.pipeline.notification.Notification;
import io.debezium.pipeline.notification.channels.NotificationChannel;
import io.debezium.pipeline.signal.SignalRecord;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HttpNotificationChannel implements NotificationChannel {
    public static final String CHANNEL_NAME = "http";
    public static final Queue<SignalRecord> SIGNALS = new ConcurrentLinkedQueue<>();

    @Override
    public String name() {
        return CHANNEL_NAME;
    }

    @Override
    public void init(CommonConnectorConfig config) {
    }

    @Override
    public void send(Notification notification) {

    }

    @Override
    public void close() {
        SIGNALS.clear();
    }
}
