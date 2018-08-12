package crawler.pools;

import crawler.http.Response;

public class ResponsePool extends Pool<Response> {

    public ResponsePool(int size) {
        super(size);
    }
}
