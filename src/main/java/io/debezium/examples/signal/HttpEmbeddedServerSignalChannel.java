package io.debezium.examples.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.CommonConnectorConfig;
import io.debezium.pipeline.signal.SignalRecord;
import io.debezium.pipeline.signal.channels.SignalChannelReader;
import io.debezium.pipeline.signal.channels.jmx.JmxSignalChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HttpEmbeddedServerSignalChannel implements SignalChannelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxSignalChannel.class);
    public static final String CHANNEL_NAME = "http-server";
    private static final List<SignalRecord> SIGNALS = new ArrayList<>();
    public CommonConnectorConfig connectorConfig;

    private final int PORT = 8080;

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
            // Get the signals from the endpoint
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:" + PORT + "/signals"))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String signalJson = response.body();

                ObjectMapper mapper = new ObjectMapper();
                String[] signalLines = signalJson.split("\n");

                for (String signalLine : signalLines) {
                    SignalRecord signal = mapper.readValue(signalLine, SignalRecord.class);
                    SIGNALS.add(signal);
                }
            }
        } catch (IOException | InterruptedException | RuntimeException e) {
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