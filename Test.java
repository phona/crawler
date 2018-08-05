package crawler;

import static crawler.Util.URLPool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Test {
    public static String testUrl = "https://www.baidu.com";

    public static void main(String[] args) throws Exception {
        URLPool pool = new URLPool(1);
        pool.push(testUrl);
        Producer p = new Producer(pool);
        p.setRequestHeader("Accept", 
        "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
        + "application/x-shockwave-flash, application/xaml+xml, "
        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
        + "application/x-ms-application, application/vnd.ms-excel, "
        + "application/vnd.ms-powerpoint, application/msword, */*");
        p.setRequestHeader("Accept-Language", "zh-CN");
        p.setRequestHeader("Charset", "UTF-8");

        htmlTest(p.get(5 * 1000));
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