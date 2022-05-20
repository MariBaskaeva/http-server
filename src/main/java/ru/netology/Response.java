package ru.netology;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Response {
    private final int status;
    private final String statusMessage;
    private final String contentType;
    private final byte[] content;

    public Response(int status, String statusMessage, String contentType, byte[] content) {
        this.status = status;
        this.statusMessage = statusMessage;
        this.contentType = contentType;
        this.content = content;
    }
    public void send(BufferedOutputStream out) throws IOException {
        out.write(toString().getBytes());
        out.write(content);
    }

    @Override
    public String toString(){
        return "HTTP/1.1 " + " " + status + " " + statusMessage + "\r\n" +
                (content == null ? "" : ("Content-Length: " + content.length + "\r\n" +
                        "Content-Type: " + contentType + "\r\n")) +
                "Connection: close\r\n" +
                "\r\n";
    }
}
