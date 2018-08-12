package crawler.test;

import crawler.impl.TaskPool;
import crawler.pools.ResponsePool;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;

import crawler.http.Request;
import crawler.pools.RequestPool;
import static crawler.Settings.poolMaxHolding;

public class ThreadPoolTest {

    // TODO: 需要设置，什么情况下会停止所有线程，还要注意处理，防止死锁
    public static void main(String[] args) {
        // initialize
        RequestPool requestPool = createRequestPool();
        ResponsePool responsePool = new ResponsePool(1000);
        TaskPool taskPool = new TaskPool(10);
        final Decoder decoder = Base64.getDecoder();

        taskPool.initialize(requestPool, responsePool);
        taskPool.initHandler(response-> {
            System.out.println(response.getUrl());
            return response;
        }, (response, doc)-> {
            ArrayList<String> arr = new ArrayList<>();
            Elements imgHashs = doc.getElementsByClass("img-hash");

            imgHashs.forEach(content -> {
                String tmp = "http:" + new String(decoder.decode(content.text()));
                arr.add(tmp);
                System.out.println(tmp);
            });

            response.getMeta().setUrls(arr);
        });
        taskPool.testGo();
    }

    private static RequestPool createRequestPool() {
        RequestPool Pool = new RequestPool(1000);
        try {
            String begin = "http://jandan.net/ooxx/page-1#comments";
            Pool.add(new Request(begin));
        } catch (ProtocolException | MalformedURLException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return Pool;
    }
}

