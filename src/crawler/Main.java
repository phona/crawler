package crawler;

import crawler.http.Request;
import crawler.util.Pools.JobPool;
import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Base64;
import java.util.Base64.Decoder;

import static crawler.Settings.poolMaxHolding;
import static crawler.util.Pools.RequestPool;

public class Main {

    // TODO: 需要设置，什么情况下会停止所有线程，还要注意处理，防止死锁
    public static void main(String[] args) throws InterruptedException {
        // initialize
        RequestPool rpool = createRequestPool();
        JobPool jobPool = new JobPool(10);
        final Decoder decoder = Base64.getDecoder();

        jobPool.initialize(rpool);
        jobPool.initHandler(response-> {
            System.out.println(response.getUrl());
            return response;
        }, doc-> {
            Elements imgHashs = doc.getElementsByClass("img-hash");
            imgHashs.forEach(content -> {
                System.out.println(new String(decoder.decode(content.text())));
            });
        });
        jobPool.go();
    }

    private static RequestPool createRequestPool() {
        RequestPool Pool = new RequestPool(poolMaxHolding);
        try {
            String begin = "http://jandan.net/ooxx/page-1#comments";
            Pool.add(new Request(begin));
        } catch (ProtocolException | MalformedURLException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return Pool;
    }
}
