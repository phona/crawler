package crawler;

import org.jsoup.select.Elements;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;

import crawler.http.Request;
import crawler.pools.JobPool;
import crawler.pools.RequestPool;
import static crawler.Settings.poolMaxHolding;

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
        }, (response, doc)-> {
            ArrayList<String> arr = new ArrayList<>();
            Elements imgHashs = doc.getElementsByClass("img-hash");

            imgHashs.forEach(content -> {
                String tmp = "http:" + new String(decoder.decode(content.text()));
                arr.add(tmp);
                System.out.println(tmp);
            });

//            response.getMeta().setUrls(arr);
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
