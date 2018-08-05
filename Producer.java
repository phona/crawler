package crawler;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import static crawler.Util.URLPool;
import static crawler.Util.InvalidURLException;

public class Producer {
    private URLPool pool;
    private URL url;
    private HttpURLConnection conn;
    private HashMap<String, String> headers = new HashMap<>();
    private HashMap<String, String> datas = new HashMap<>();

    public Producer(URLPool pool) {
        this.pool = pool; 
    }

    public void setRequestHeader(String k, String v) {
        headers.put(k, v);
    }

    public String getRequestHeader(String k) {
        return headers.get(k);
    }

    public void setQueryString(String k, String v) {
        datas.put(k, v);
    }

    private void prepareParams(String newUrl, String method, int timeout) 
            throws InvalidURLException, IOException, ProtocolException {
        try {
            url = new URL(newUrl);
            conn = (HttpURLConnection) url.openConnection();
            // set HttpRequest parameters
            headers.forEach((k, v) -> conn.setRequestProperty(k, v));
            conn.setRequestMethod(method);
            conn.setConnectTimeout(timeout);
        } catch (MalformedURLException ex) {
            throw new InvalidURLException(ex);
        }
    }

    private String serialize() {
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

    public InputStream get(int timeout) throws IOException, InvalidURLException {
        return get(pool.get(), timeout);
    }
    
    public InputStream post(int timeout) throws IOException, InvalidURLException {
        return post(pool.get(), timeout);
    }

    private InputStream get(String newUrl, int timeout) throws IOException, InvalidURLException {
        String queryString = serialize();
        if (queryString == "") {
            prepareParams(newUrl, "GET", timeout);
        } else {
            prepareParams(newUrl + "?" + queryString, "GET", timeout);
        }

        return conn.getInputStream();
    }

    private InputStream post(String newUrl, int timeout) throws IOException {
        conn.setDoOutput(true);
        conn.setDoInput(true);
        try(
            PrintWriter out = new PrintWriter(conn.getOutputStream());
        ) {
           out.print(serialize()); 
           out.flush();
        }

        return conn.getInputStream();
    }

    public int getFileSize() {
        return conn.getContentLength();
    }
}