package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Response {
    private final int status;
    private final String statusMessage;
    private final String contentType;
    private final long contentLength;
    private final byte[] content;

    public Response(int status, String statusMessage, String contentType, long contentLength, byte[] content) {
        this.status = status;
        this.statusMessage = statusMessage;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.content = content;
    }
    public void send(BufferedOutputStream out) throws IOException {
        out.write(toString().getBytes());
        out.write(content);
        out.flush();
    }
    public void send(BufferedOutputStream out, Path filePath) throws IOException {
        out.write(toString().getBytes());
        Files.copy(filePath, out);
        out.flush();
    }

    @Override
    public String toString(){
        return "HTTP/1.1 " + status + " " + statusMessage + "\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                (status == 404 ? "" : ("Content-Type: " + contentType + "\r\n")) +
                "Connection: close\r\n" +
                "\r\n";
    }
}
