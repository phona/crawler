package crawler.test;

import crawler.pools.RequestPool;
import crawler.impl.Sender;
import crawler.http.Request;
import crawler.http.Response;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SenderTest {
    public static String testUrl = "https://www.baidu.com";

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

        pool.add(testReq);
//        Sender p = new Sender(pool);
//        p.get(5 * 1000);

        // htmlTest(p.toConsume().getInputStream());
//        Response r = p.toConsume();
//        System.out.println(r.getContentType());
//        System.out.println(r.getContentLength());
    }

    public static void htmlTest(InputStream input) {
        String result = "";
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(input, "utf-8"));
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
            System.out.println(result);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}