package crawler.http;

import crawler.util.Utils;
import crawler.util.Utils.Adaptor;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Response {
    private String url;
    private int contentLength;
    private String contentType;
    private int fileLength;
    private InputStream input;
    // 设置上下文参数，通过这个能实现不断解析并发起请求
    private META meta;

    public Response(String url, int contentLength, String contentType, int fileLength, InputStream input) {
        this.url = url;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.fileLength = fileLength;
        this.input = input;
        this.meta = new META();
    }

    public class META {
        private ArrayList<String> urls = null;

        public void setUrls(ArrayList<String> urls) {
            this.urls = urls;
        }

        public ArrayList<String> getUrls() {
            return urls;
        }
    }

    public META getMeta() {
        return meta;
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