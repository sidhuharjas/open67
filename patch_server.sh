python3 -c '
import sys
content = open("src/main/java/com/open67/web/Open67WebServer.java").read()

# Add Context
content = content.replace("server.createContext(\"/api/start\", new StartHandler());", "server.createContext(\"/api/start\", new StartHandler());\n            server.createContext(\"/api/calibrate\", new CalibrateHandler());")

handler = """
    private final class CalibrateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                detector.startCalibration();
                sendJsonResponse(exchange, 200, "{\"status\":\"calibrating\"}");
            } else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        }
    }
"""

content = content.replace("public final class Open67WebServer {", "public final class Open67WebServer {" + handler)

open("src/main/java/com/open67/web/Open67WebServer.java", "w").write(content)
'
