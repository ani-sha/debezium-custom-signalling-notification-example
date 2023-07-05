package io.debezium.examples.signal;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.config.CommonConnectorConfig;
import io.debezium.pipeline.signal.SignalRecord;
import io.debezium.pipeline.signal.channels.SignalChannelReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpSignalChannel implements SignalChannelReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSignalChannel.class);
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
        LOGGER.info("Reading signaling events from endpoint");

        try {
            String requestUrl = "http://localhost:1080/api/signal?code=10969";
            URL url = new URL(requestUrl);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                ObjectMapper mapper = new ObjectMapper();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    SignalRecord signal = mapper.readValue(line, SignalRecord.class);
                    SIGNALS.add(signal);
                    LOGGER.trace("Signal '{}' from endpoint", signal.toString());
                }
                reader.close();
            } else {
                LOGGER.warn("Error while reading signaling events from endpoint: {}", status);
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