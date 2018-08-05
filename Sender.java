package crawler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;

import static crawler.Utils.Pools.RequestPool;
import crawler.models.Producer;
import crawler.models.CustomInterface.HttpRequestable;
import crawler.Utils.CustomExceptions.InvalidURLException;
import crawler.Utils.CustomExceptions.InvalidURLException;
import crawler.Utils.CustomExceptions.PoolOverFlowException;
import crawler.http.Request;
import crawler.http.Response;

public class Sender extends Producer<Response> implements HttpRequestable {
    private RequestPool pool;
    private HttpURLConnection conn;
    private InputStream input;
    private String url;

    public Sender(RequestPool pool) {
        this.pool = pool; 
    }
    
    public void addRequest(Request req) throws PoolOverFlowException {
        this.pool.push(req);
    }

    public void get(int timeout) throws IOException, InvalidURLException {
        get(pool.get(), timeout);
    }
    
    public void post(int timeout) throws IOException, InvalidURLException {
        post(pool.get(), timeout);
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