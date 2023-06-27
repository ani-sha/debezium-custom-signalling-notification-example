package model;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class SignalServer {
    private static final String response = "{\"type\":\"execute-snapshot\",\"data\":{\"data-collections\":[\"schema1.table1\",\"schema1.table2\"],\"type\":\"INCREMENTAL\"}}";
    public static void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("debezium-channel", 8000), 0);
        server.createContext("/signal", new SignalHandler(response));
        server.setExecutor(null);
        server.start();
    }

    static class SignalHandler implements HttpHandler {
        private final String response;
        SignalHandler(String response) {
            this.response = response;
        }

        public void handle(HttpExchange exchange) throws IOException {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
