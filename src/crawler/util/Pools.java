package crawler.util;

import crawler.abstractmodels.CustomInterface.CustomRunable;
import crawler.http.Request;
import crawler.http.Response;
import crawler.impl.Handler;
import crawler.impl.Sender;
import crawler.util.CustomExceptions.PoolOverFlowException;

import java.util.ArrayDeque;

public class Pools {
    /**
     * @category Pools
     */
    public static class Pool <E>{
        private ArrayDeque<E> arr;
        private final int size;

        public Pool(int size) {
            this.arr = new ArrayDeque<>(size);
            this.size = size;
        }

        public int getSize() {
            return arr.size();
        }

        public synchronized void add(E e) throws PoolOverFlowException {
            if (this.arr.size() < size) {
                this.arr.add(e);
            } else {
                throw new PoolOverFlowException();
            }
        }

        public synchronized E get() {
            return this.arr.poll();
        };
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
                return get();
            } catch (IndexOutOfBoundsException ex) {
                return null;
            }
        }

        public synchronized void push(Request req) throws PoolOverFlowException {
            if (getSize() < getSize()) {
                add(req);
            } else {
                throw new PoolOverFlowException("The pool is full, can push item anymore.");
            }
        }
    }

    /**
     * 用于生产者与消费者之间的通信
     */
    public static class JobPool extends Pool<Response> {
        private Sender sender;
        private Handler handler;
        private Thread[] senders;
        private Thread[] handlers;

        /**
         * 设置job队列上限
         * @param size
         */
        public JobPool(int size) {
            super(size);
        }

        public void setPeers(Sender sender, Handler handler) {
            this.sender = sender;
            this.handler = handler;
        }

        public void initSenders(int size, CustomRunable runnable) {
            System.out.println("Setting Sender size is " + size + ".");
            senders = new Thread[size];

            for (int i = 0; i < size; i++) {
                senders[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                });
            }
        }

        public void initHandler(int size, CustomRunable runnable) {
            System.out.println("Setting handler size is " + size + ".");
            handlers = new Thread[size];

            for (int i = 0; i < size; i++) {
                senders[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                });
            }
        }

        public void go() throws InterruptedException {
            System.out.println("Start tasks.");

            for (int i = 0; i < senders.length; i++) {
                senders[i].start();
                senders[i].join(1000);
            }

            for (int i = 0; i < handlers.length; i++) {
                handlers[i].start();
                handlers[i].join(1000);
            }
        }
    }
}