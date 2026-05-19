package com.open67;

import com.open67.web.Open67WebServer;
import nu.pattern.OpenCV;

public final class Open67Application {

    private Open67Application() {
    }

    public static void main(String[] args) {
        OpenCV.loadLocally();
        int port = Integer.parseInt(System.getenv().getOrDefault("OPEN67_PORT", "8080"));
        Open67WebServer webServer = new Open67WebServer(port);
        webServer.start();
    }
}