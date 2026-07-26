package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class SimpleHttpServer {

    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/hello", SimpleHttpServer::handleHello);
        server.createContext("/users", SimpleHttpServer::handleUsers);
        server.createContext("/echo",  SimpleHttpServer::handleEcho);

        server.setExecutor(null); // dùng executor mặc định
        server.start();
        System.out.println("HTTP REST Server is running on port " + PORT);
        System.out.println("Endpoints:");
        System.out.println("  GET  http://localhost:" + PORT + "/hello");
        System.out.println("  GET  http://localhost:" + PORT + "/users");
        System.out.println("  POST http://localhost:" + PORT + "/echo");
    }

    // ----------------------------------------------------------------
    // GET /hello
    // ----------------------------------------------------------------
    private static void handleHello(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        System.out.println("[GET /hello] request received");
        String body = "{\"message\":\"Hello from Java HTTP Server!\"}";
        sendResponse(exchange, 200, body);
    }

    // ----------------------------------------------------------------
    // GET /users
    // ----------------------------------------------------------------
    private static void handleUsers(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        System.out.println("[GET /users] request received");
        String body = """
                [
                  {"id": 1, "userName": "Alice", "isActive": true,  "role": ["Admin"]},
                  {"id": 2, "userName": "Bob",   "isActive": true,  "role": ["User"]},
                  {"id": 3, "userName": "Carol",  "isActive": false, "role": ["User", "Moderator"]}
                ]
                """;
        sendResponse(exchange, 200, body);
    }

    // ----------------------------------------------------------------
    // POST /echo
    // ----------------------------------------------------------------
    private static void handleEcho(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
            return;
        }
        InputStream is = exchange.getRequestBody();
        String requestBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("[POST /echo] body: " + requestBody);
        String body = "{\"echo\":" + (requestBody.isBlank() ? "\"(empty)\"" : requestBody) + "}";
        sendResponse(exchange, 200, body);
    }

    // ----------------------------------------------------------------
    // Helper: gửi response JSON
    // ----------------------------------------------------------------
    private static void sendResponse(HttpExchange exchange, int statusCode, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
