package crawler;

import crawler.http.Request;
import crawler.util.Pools.JobPool;

import java.net.MalformedURLException;
import java.net.ProtocolException;

import static crawler.Settings.poolMaxHolding;
import static crawler.util.Pools.RequestPool;

public class Main {
    private static String begin = "http://jandan.net/ooxx";

    // TODO: 需要设置，什么情况下会停止所有线程，还要注意处理，防止死锁
    public static void main(String[] args) throws InterruptedException {
        // initialize
        RequestPool rpool = createRequestPool();
        JobPool jobPool = new JobPool(10);
        jobPool.initialize(rpool);
        jobPool.initHandler(response-> {
            System.out.println(response.getUrl());
            return response;
        }, doc-> {
            System.out.println(doc.title());
        });
        jobPool.go();
    }

    private static RequestPool createRequestPool() {
        RequestPool Pool = new RequestPool(poolMaxHolding);
        try {
            Pool.add(new Request(begin));
        } catch (ProtocolException | MalformedURLException | InterruptedException ex) {
            ex.printStackTrace();
        }
        return Pool;
    }
}
