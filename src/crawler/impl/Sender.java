package crawler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.UnknownHostException;

import crawler.pools.RequestPool;
import crawler.abstractmodels.Producer;
import crawler.abstractmodels.CustomInterface.HttpRequestable;
import crawler.util.CustomExceptions.InvalidURLException;
import crawler.http.Request;
import crawler.http.Response;

public class Sender extends Producer<Response> {
    private HttpURLConnection conn;
    private String url;
    private InputStream input;
    private Request request;
    private int timeout;

    /**
     * 当请求成功后，通过这个方法获取返回的数据
     */
    public Sender(Request request, int timeout) {
        this.request = request;
        this.timeout = timeout;
    }

    @Override
    public Response toConsume() {
        return new Response(url, conn.getContentLength(), conn.getContentType(), 1024, input);
    }

    public Response get() throws IOException {
        _get(request, timeout);
        return toConsume();
    }

    public Response post() throws IOException {
        _post(request, timeout);
        return toConsume();
    }

    private void _get(Request req, int timeout) throws IOException {
        String queryString = req.getQueryString();
        url = req.getURL();
        try {
            if (queryString == "") {
                prepareParams(req, "GET", timeout);
            } else {
                prepareParams(req, "GET", timeout);
            }
        } catch (InvalidURLException ex) {
            ex.printStackTrace();
        }
        try {
            input = conn.getInputStream();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void prepareParams(Request req, String method, int timeout)
            throws InvalidURLException, IOException {
        try {
            conn = req.getConnection();
            // set HttpRequest parameters
            req.getAllRequestHeaders().forEach((k, v) -> conn.setRequestProperty(k, v));
            conn.setRequestMethod(method);
            conn.setConnectTimeout(timeout);
        } catch (MalformedURLException | ProtocolException ex) {
            throw new InvalidURLException(ex);
        }
    }

    private void _post(Request req, int timeout) throws IOException {
        conn = req.getConnection();
        url = req.getURL();
        conn.setDoOutput(true);
        conn.setDoInput(true);
        try(
            PrintWriter out = new PrintWriter(conn.getOutputStream());
        ) {
           out.print(req.getQueryString()); 
           out.flush();
        }

        input = conn.getInputStream();
    }

    public int getFileSize() {
        return conn.getContentLength();
    }
}