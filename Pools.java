package crawler.Utils;

import java.util.ArrayList;

import crawler.Utils.CustomExceptions.PoolOverFlowException;
import crawler.Utils.CustomExceptions.InvalidURLException;
import crawler.http.Request;

public class Pools {
    /**
     * @category Pools
     */
    public static abstract class Pool <E>{
        private ArrayList<E> arr;
        private final int size;

        public Pool(int size) {
            this.arr = new ArrayList<>(size); 
            this.size = size;
        }

        public ArrayList<E> getArr() {
            return arr;
        }

        public int getSize() {
            return size;
        }

        public abstract E get();

        public abstract void push(E url) throws PoolOverFlowException;
    }

    /**
     * 用于实现url队列池
     * 也用于当再次解析html页面时获取到页面里的链接继续放进url队列池
     */
    public static class RequestPool extends Pool<Request>{
        public RequestPool(int size) {
            super(size);
        }

        public synchronized Request get() {
            try {
                return getArr().remove(0);
            } catch (IndexOutOfBoundsException ex) {
                return null;
            }
        }

        public synchronized void push(Request req) throws PoolOverFlowException {
            if (getArr().size() < getSize()) {
                getArr().add(req); 
            } else {
                throw new PoolOverFlowException("The pool is full, can push item anymore.");
            }
        }
    }

    /**
     * 用于生产者与消费者之间的通信
     */
    public static class PipePool extends Pool<String> {
        public PipePool(int size) {
            super(size);
        } 

        public synchronized String get() {
            try {
                return getArr().remove(0);
            } catch (IndexOutOfBoundsException ex) {
                return null;
            }
        }

        public synchronized void push(String url) throws PoolOverFlowException {
            if (getArr().size() < getSize()) {
                getArr().add(url); 
            } else {
                throw new PoolOverFlowException("The pool is full, can push item anymore.");
            }
        }
    }
}