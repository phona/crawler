package crawler.pools;

import crawler.http.Request;

/**
 * 用于实现url队列池
 * 也用于当再次解析html页面时获取到页面里的链接继续放进url队列池
 */
public class RequestPool extends Pool<Request> {
    public RequestPool(int size) {
        super(size);
    }
}
