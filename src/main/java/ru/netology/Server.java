package ru.netology;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final ExecutorService pool;
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private final int THREAD_POOL_COUNT = 64;

    public Server() {
        pool = Executors.newFixedThreadPool(THREAD_POOL_COUNT);
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                final var socket = serverSocket.accept();
                pool.submit(() -> connectionProcessing(socket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void connectionProcessing(Socket socket) {
        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                return;
            }
            final var path = parts[1];
            sendMsg(path, out);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void sendMsg(String path, BufferedOutputStream out) throws IOException {
        ResponseBuilder responseBuilder = new ResponseBuilder();

        final var filePath = Path.of(".", "public", path);
        final var mimeType = Files.probeContentType(filePath);

        if (!validPaths.contains(path)) {
            responseBuilder.setStatus(404)
                    .setStatusMessage("Not Found")
                    .build()
                    .send(out);
            return;
        }

        responseBuilder.setStatus(200)
                .setStatusMessage("OK")
                .setContentType(mimeType);

        if (path.equals("/classic.html")) {
            final var template = Files.readString(filePath);
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();

            responseBuilder.setContent(content)
                    .setContentLength(content.length)
                    .build()
                    .send(out);
            return;
        }

        Response response = responseBuilder.setContentLength(Files.size(filePath))
                .build();
        System.out.println(response);
        response.send(out, filePath);
    }
}
