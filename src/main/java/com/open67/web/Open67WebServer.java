package com.open67.web;

import com.open67.vision.Gesture67Detector;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public final class Open67WebServer {

    private final int port;
    private final Gesture67Detector detector = new Gesture67Detector();
    private final GestureSession session = new GestureSession();

    public Open67WebServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            server.createContext("/", new IndexHandler());
            server.createContext("/api/frame", new FrameHandler());
            server.createContext("/api/state", new StateHandler());
            server.createContext("/api/reset", new ResetHandler());
            server.createContext("/api/start", new StartHandler());
            server.setExecutor(Executors.newCachedThreadPool(runnable -> {
                Thread thread = new Thread(runnable, "open67-web");
                thread.setDaemon(false);
                return thread;
            }));
            server.start();

            System.out.println("Open67 web app running at http://localhost:" + port);
            System.out.println("If you are in Codespaces, forward port " + port + " and open the browser URL.");
        } catch (IOException ioException) {
            throw new IllegalStateException("Failed to start web server", ioException);
        }
    }

    private final class IndexHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }

            byte[] body = WebAssets.indexHtml().getBytes(StandardCharsets.UTF_8);
            sendBytes(exchange, 200, body, "text/html; charset=utf-8");
        }
    }

    private final class StateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }

            sendJson(exchange, 200, session.toJson());
        }
    }

    private final class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }

            synchronized (session) {
                session.reset();
            }
            sendJson(exchange, 200, session.toJson());
        }
    }

    private final class StartHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }

            // Optionally accept target/timer in the body
            String requestBody = new String(readAll(exchange.getRequestBody()), StandardCharsets.UTF_8);
            Map<String, String> params = parseFormEncoded(requestBody);
            int targetCount = parsePositiveInt(params.get("targetCount"), session.targetCount());
            int timerSeconds = parsePositiveInt(params.get("timerSeconds"), session.timerSeconds());

            synchronized (session) {
                session.setConfiguration(targetCount, timerSeconds);
                session.startCountdown();
            }

            sendJson(exchange, 200, session.toJson());
        }
    }

    private final class FrameHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendText(exchange, 405, "Method Not Allowed", "text/plain; charset=utf-8");
                return;
            }

            String requestBody = new String(readAll(exchange.getRequestBody()), StandardCharsets.UTF_8);
            Map<String, String> params = parseFormEncoded(requestBody);
            String imageDataUrl = params.getOrDefault("imageDataUrl", "");
            boolean hasClientSignal = "true".equalsIgnoreCase(params.getOrDefault("hasClientSignal", "false"));
            int targetCount = parsePositiveInt(params.get("targetCount"), session.targetCount());
            int timerSeconds = parsePositiveInt(params.get("timerSeconds"), session.timerSeconds());

            synchronized (session) {
                session.setConfiguration(targetCount, timerSeconds);
                GestureDetectionResultView resultView = hasClientSignal
                        ? processClientSignal(params)
                        : processFrame(imageDataUrl);
                sendJson(exchange, 200, session.toJson(resultView));
            }
        }

        private GestureDetectionResultView processClientSignal(Map<String, String> params) {
            boolean detected = "true".equalsIgnoreCase(params.getOrDefault("gestureDetected", "false"));
            int handCount = parseNonNegativeInt(params.get("handCount"), 0);
            double confidence = parseDouble(params.get("confidence"), 0.0);
            String message = params.getOrDefault("clientMessage", "Waiting for hands");

            return session.processDetection(detected, message, handCount, confidence, 0.0);
        }

        private GestureDetectionResultView processFrame(String imageDataUrl) {
            if (imageDataUrl == null || imageDataUrl.isBlank()) {
                return session.emptyResult("No frame supplied");
            }

            String base64 = stripDataUrlPrefix(imageDataUrl);
            byte[] imageBytes;
            try {
                imageBytes = java.util.Base64.getDecoder().decode(base64);
            } catch (IllegalArgumentException illegalArgumentException) {
                return session.emptyResult("Invalid frame data");
            }

            Mat encoded = new Mat(1, imageBytes.length, org.opencv.core.CvType.CV_8UC1);
            encoded.put(0, 0, imageBytes);
            Mat frame = Imgcodecs.imdecode(encoded, Imgcodecs.IMREAD_COLOR);
            encoded.release();

            if (frame.empty()) {
                return session.emptyResult("Unable to decode frame");
            }

            try {
                return session.processFrame(frame, detector);
            } finally {
                frame.release();
            }
        }
    }

    private String stripDataUrlPrefix(String value) {
        int commaIndex = value.indexOf(',');
        return commaIndex >= 0 ? value.substring(commaIndex + 1) : value;
    }

    private Map<String, String> parseFormEncoded(String body) {
        Map<String, String> params = new LinkedHashMap<>();
        if (body == null || body.isBlank()) {
            return params;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int index = pair.indexOf('=');
            if (index < 0) {
                continue;
            }

            String key = decode(pair.substring(0, index));
            String value = decode(pair.substring(index + 1));
            params.put(key, value);
        }

        return params;
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    private int parsePositiveInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(1, parsed);
        } catch (NumberFormatException numberFormatException) {
            return fallback;
        }
    }

    private int parseNonNegativeInt(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return Math.max(0, parsed);
        } catch (NumberFormatException numberFormatException) {
            return fallback;
        }
    }

    private double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException numberFormatException) {
            return fallback;
        }
    }

    private byte[] readAll(InputStream inputStream) throws IOException {
        return inputStream.readAllBytes();
    }

    private void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        sendBytes(exchange, statusCode, json.getBytes(StandardCharsets.UTF_8), "application/json; charset=utf-8");
    }

    private void sendText(HttpExchange exchange, int statusCode, String text, String contentType) throws IOException {
        sendBytes(exchange, statusCode, text.getBytes(StandardCharsets.UTF_8), contentType);
    }

    private void sendBytes(HttpExchange exchange, int statusCode, byte[] body, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, body.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(body);
        }
    }
}