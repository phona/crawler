package crawler;

import crawler.http.Request;
import crawler.http.Response;
import crawler.impl.Handler;
import crawler.impl.Sender;
import crawler.util.CustomExceptions;
import crawler.util.Pools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import static crawler.Settings.poolMaxHolding;
import static crawler.util.Pools.RequestPool;

public class Main {
    private static String begin = "http://jandan.net/ooxx";

    public static void main(String[] args) throws IOException, CustomExceptions.InvalidURLException, CustomExceptions.NoPathFoundException {
        // initialize
        RequestPool rpool = createRequestPool();
        Sender sender = new Sender(rpool);

        Pools.Pool<Response> handlePool = new Pools.Pool<Response>(10);

        Handler handler = new Handler();
        handler.setHTMLParse(doc -> {
            doc.getElementsByTag("img");
        });

        handler.addItem(sender.get(5 * 1000));
        handler.handleItem();
    }

    private static RequestPool createRequestPool() {
        RequestPool Pool = new RequestPool(poolMaxHolding);
        try {
            Pool.push(new Request(begin));
        } catch (ProtocolException | MalformedURLException | CustomExceptions.PoolOverFlowException ex) {
            ex.printStackTrace();
        }
        return Pool;
    }
}
