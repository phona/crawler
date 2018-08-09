package crawler.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import static crawler.Settings.requestHead;

/**
 * 定义一个Request对象
 *
 */
public class Request {
    private URL url;
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> datas = new HashMap<>();

    {
        headers.putAll(requestHead);
    }

    public Request(String url) throws ProtocolException, MalformedURLException{
        this.url = new URL(url);
    }

    public HttpURLConnection getConnection() throws IOException {
        return (HttpURLConnection)url.openConnection();
    }

    public void setRequestHeader(String k, String v) {
        headers.put(k, v);
    }

    public String getRequestHeader(String k) {
        return headers.get(k);
    }

    public HashMap<String, String> getAllRequestHeaders() {
        return headers;
    }

    public void setQueryString(String k, String v) {
        datas.put(k, v);
    }

    public String getQueryString() {
        String queryString = "";
        int count = 0;

        for (String k : datas.keySet()) {
            if (++count < datas.size()) {
                queryString += k + "=" + datas.get(k) + "&";
            } else {
                queryString += k + "=" + datas.get(k);
            }
        }

        return queryString;
    }

    public String getURL() {
        return url.getFile();
    }
}