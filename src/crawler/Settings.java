package crawler;

import java.util.HashMap;

/**
 * 系统参数配置
 */
public class Settings {
    final public static String imgPath = ".";
    final public static int poolMaxHolding = 10;
    final public static int sleep = 1;
    final public static HashMap<String, String> requestHead = new HashMap<>();

    // 设置默认请求头
    static {
        requestHead.put("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, "
                        + "application/x-shockwave-flash, application/xaml+xml, "
                        + "application/vnd.ms-xpsdocument, application/x-ms-xbap, "
                        + "application/x-ms-application, application/vnd.ms-excel, "
                        + "application/vnd.ms-powerpoint, application/msword, */*");
        requestHead.put("Accept-Language", "zh-CN");
        requestHead.put("Charset", "UTF-8");
    }
}
