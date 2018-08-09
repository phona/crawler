package crawler.util;

import crawler.http.Request;
import crawler.http.Response;
import crawler.impl.Handler;
import crawler.impl.Sender;
import crawler.util.CustomExceptions.PoolOverFlowException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static crawler.Settings.*;
import static crawler.abstractmodels.CustomInterface.*;
import static crawler.util.CustomExceptions.*;

public class Pools {
    /**
     * @category Pools
     */
    public static class Pool <E>{
        private ArrayDeque<E> pool;
        private final int size;

        public Pool(int size) {
            this.pool = new ArrayDeque<>(size);
            this.size = size;
        }

        public ArrayDeque<E> getPool() {
            return pool;
        }

        public int getSize() {
            return pool.size();
        }

        public synchronized void add(E e) throws PoolOverFlowException {
            if (this.pool.size() < size) {
                this.pool.add(e);
            } else {
                throw new PoolOverFlowException();
            }
        }

        public synchronized E get() throws PoolNotSufficientException {
            if (pool.size() > 0) {
                return this.pool.poll();
            } else {
                throw new PoolNotSufficientException();
            }
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
    }

    /**
     * 用于生产者与消费者之间的通信
     */
    public static class JobPool extends Pool<Response> {
        private RequestPool requestPool;
        private Thread[] senders;
        private Thread[] handlers;

        /**
         * 设置job队列上限
         * @param size
         */
        public JobPool(int size) {
            super(size);
        }

        public void initialize(Pools.RequestPool requestPool) {
            this.requestPool = requestPool;
            initSenders();
        }

        private void initSenders() {
            System.out.println("Setting Sender size is " + requests + ".");
            senders = new Thread[requests];

            for (int i = 0; i < requests; i++) {
                senders[i] = new Thread(new Runnable() {
                    Sender sender = new Sender(requestPool);

                    @Override
                    public void run() {
                        try {
                            // getRequest与get(int)两个方法会抛出相同的异常
                            Request request = sender.getRequest();
                            System.out.println(Thread.currentThread().getName()
                                                + " is construct request: "
                                                + request.getURL());

                            Response response = sender.get(timeout);
                            System.out.println(Thread.currentThread().getName()
                                    + " receive a response from: "
                                    + request.getURL());

                            add(response);
                            System.out.println("Add Job, Job pool size now is: " + getSize());

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (PoolNotSufficientException e) {
                            try {
                                this.wait();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        } catch (PoolOverFlowException e) {
                            this.notifyAll();
                        }
                    }
                });
            }
        }

        public void initHandler(CustomParser parser, HTMLParse htmlParser) {
            System.out.println("Setting handler size is " + parsers + ".");
            handlers = new Thread[parsers];

            for (int i = 0; i < parsers; i++) {
                senders[i] = new Thread(new Runnable() {
                    Handler handler = new Handler(htmlParser);

                    @Override
                    public void run() {
                        Response response = null;
                        try {
                            response = get();
                            System.out.println(Thread.currentThread().getName()
                                    + " is consume: " + response.getUrl());
                            Response parseResponse = parser.run(response);
                            handler.addItem(parseResponse);
                            handler.handleItem();
                            ArrayList<String> urls = parseResponse.getMeta().getUrls();

                            if (parseResponse.getMeta().getUrls() != null) {
                                urls.forEach(url-> {
                                    try {
                                        requestPool.add(new Request(url));
                                    } catch (PoolOverFlowException e) {
                                        try {
                                            JobPool.this.wait();
                                        } catch (InterruptedException e1) {
                                            e1.printStackTrace();
                                        }
                                    } catch (ProtocolException | MalformedURLException e) {
                                        e.printStackTrace();
                                    }
                                });
                            };
                        } catch (PoolNotSufficientException e) {
                            try {
                                this.wait();
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        } catch (NoPathFoundException e) {
                           e.printStackTrace();
                        }
                    }
                });
            }
        }

        public void go() throws InterruptedException {
            System.out.println("Start tasks.");

            for (Thread sender : senders) {
                sender.start();
                sender.join(1000);
            }

            for (Thread handler : handlers) {
                handler.start();
                handler.join(1000);
            }
        }
    }
}