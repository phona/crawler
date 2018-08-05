package crawler.http;

import java.io.InputStream;

public class Response {
    private int contentLength;
    private String contentType;
    private int fileLength;
    private InputStream input;

    public Response(int contentLength, String contentType, int fileLength, InputStream input) {
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.fileLength = fileLength;
        this.input = input;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public String getContentType() {
        return this.contentType;
    }

    public int getFileLength() {
        return this.fileLength;
    }

    public InputStream getInputStream() {
        return this.input;
    }
}