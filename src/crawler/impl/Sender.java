package crawler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import static crawler.util.CustomExceptions.*;
import static crawler.util.Pools.RequestPool;
import crawler.abstractmodels.Producer;
import crawler.abstractmodels.CustomInterface.HttpRequestable;
import crawler.util.CustomExceptions;
import crawler.util.CustomExceptions.InvalidURLException;
import crawler.util.CustomExceptions.PoolOverFlowException;
import crawler.http.Request;
import crawler.http.Response;

public class Sender extends Producer<Response> implements HttpRequestable {
    private RequestPool pool;
    private HttpURLConnection conn;
    private InputStream input;
    private String url;
    private Request request = null;

    public Sender(RequestPool pool) {
        this.pool = pool; 
    }

    public Request getRequest() throws PoolNotSufficientException {
        request = request == null ? pool.get() : request;
        return request;
    }

    public Response get(int timeout) throws IOException, PoolNotSufficientException {
        get(getRequest(), timeout);
        request = null;
        return toConsume();
    }
    
    public Response post(int timeout) throws IOException, PoolNotSufficientException {
        post(getRequest(), timeout);
        request = null;
        return toConsume();
    }

    /**
     * 当请求成功后，通过这个方法获取返回的数据
     */
    @Override
    public Response toConsume() {
        return new Response(url, conn.getContentLength(), conn.getContentType(), 1024, input);
    }

    @Override
    public void get(Request req, int timeout) throws IOException {
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
        input = conn.getInputStream();
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

    @Override
    public void post(Request req, int timeout) throws IOException {
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