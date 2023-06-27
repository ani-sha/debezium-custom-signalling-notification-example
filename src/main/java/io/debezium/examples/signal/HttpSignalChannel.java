package io.debezium.examples.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.CommonConnectorConfig;
import io.debezium.pipeline.signal.SignalRecord;
import io.debezium.pipeline.signal.channels.SignalChannelReader;
import io.debezium.pipeline.signal.channels.jmx.JmxSignalChannel;
import model.SignalClient;
import model.SignalServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpSignalChannel implements SignalChannelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxSignalChannel.class);
    public static final String CHANNEL_NAME = "http";
    private static final List<SignalRecord> SIGNALS = new ArrayList<>();
    public CommonConnectorConfig connectorConfig;

    @Override
    public String name() {
        return CHANNEL_NAME;
    }

    @Override
    public void init(CommonConnectorConfig connectorConfig) {
        this.connectorConfig = connectorConfig;
    }

    @Override
    public List<SignalRecord> read() {
        LOGGER.trace("Reading signaling events from endpoint");

        try {
            // Start Signal Server
            SignalServer.start();

            // Get signal response from endpoint
            String response = SignalClient.getSignalData();

            ObjectMapper mapper = new ObjectMapper();
            String[] signalLines = response.split("\n");

            for (String signalLine : signalLines) {
                SignalRecord signal = mapper.readValue(signalLine, SignalRecord.class);
                SIGNALS.add(signal);
                LOGGER.trace("Signal '{}' from endpoint", signal.toString());
            }
        } catch (IOException | RuntimeException e) {
            LOGGER.warn("Exception while preparing to process the signal '{}' from the endpoint", e.getMessage());
            e.printStackTrace();
        }
        return SIGNALS;
    }

    @Override
    public void close() {
        SIGNALS.clear();
    }
}