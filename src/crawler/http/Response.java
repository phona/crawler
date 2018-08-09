package crawler.http;

import java.io.InputStream;

public class Response {
    private String url;
    private int contentLength;
    private String contentType;
    private int fileLength;
    private InputStream input;

    public Response(String url, int contentLength, String contentType, int fileLength, InputStream input) {
        this.url = url;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.fileLength = fileLength;
        this.input = input;
    }

    public String getUrl() {
        return this.url;
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