package crawler;

import static crawler.Utils.Pools.RequestPool;
import crawler.impl.Sender;
import crawler.impl.Handler;
import crawler.http.Response;
import crawler.http.Request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HandlerTest {
    public static String testUrl = "http://mm.chinasareview.com/wp-content/uploads/2017a/04/18/01.jpg";

    public static void main(String[] args) throws Exception {
        Request testReq = new Request(testUrl);
        RequestPool pool = new RequestPool(1);

        testReq.setRequestHeader("Accept", 
        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
        + "application/x-shockwave-flash, application/xaml+xml, "
        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
        + "application/x-ms-application, application/vnd.ms-excel, "
        + "application/vnd.ms-powerpoint, application/msword, */*");
        testReq.setRequestHeader("Accept-Language", "zh-CN");
        testReq.setRequestHeader("Charset", "UTF-8");

        pool.push(testReq);
        Sender p = new Sender(pool);
        p.get(5 * 1000);

        Response r = p.toConsume();
        // System.out.println(r.getContentLength());
        // System.out.println(r.getContentType().indexOf("image"));
        Handler handler = new Handler(p.toConsume());
        handler.setPath("/Users/Heweitao/Desktop/SelfCoding/java/crawler");
        handler.parse();
    }
}