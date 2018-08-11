package crawler.util;

import crawler.http.Request;
import crawler.http.Response;
import crawler.impl.Handler;
import crawler.impl.Sender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static crawler.Settings.*;
import static crawler.abstractmodels.CustomInterface.*;
import static crawler.util.CustomExceptions.*;

public class Pools {
    /**
     * @category Pools
     * 可以考虑使用注解来简化加锁释放锁的操作
     */
    public static class Pool <E>{
        private ArrayDeque<E> pool;
        private final int size;
        private int unfinished;
        private final Lock lock;
        private final Condition notEmpty;
        private final Condition notFull;
        private final Condition allTaskDone;


        public Pool(int size) {
            _init(size);
            this.size = size;
            this.unfinished = 0;

            lock = new ReentrantLock();
            notEmpty = lock.newCondition();
            notFull = lock.newCondition();
            allTaskDone = lock.newCondition();
        }

        // not Empty
        public void add(E e) throws InterruptedException {
            lock.lock();
            if (notFinishedSize() < getMaxSize()) {
                _add(e);
            } else {
                notEmpty.await();
            }
            notFull.signal();
            lock.unlock();
        }

        // not full
        public synchronized E get() throws InterruptedException {
            lock.lock();
            try {
                E item;
                if (pool.size() > notFinishedSize()) {
                    item = _get();
                } else {
                    notFull.await();
                    item = _get();
                }
                unfinished++;
                notEmpty.signal();
                return item;
            } finally {
                lock.unlock();
            }
        }

        public void taskDone() {
            lock.lock();
            try {
                int unfinishedTask = unfinished - 1;
                if (unfinishedTask <= 0) {
                    if (unfinishedTask < 0) throw new IndexOutOfBoundsException("Too many taskDone was called.");
                }
                this.unfinished = unfinishedTask;
                allTaskDone.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public boolean isEmpty() {
            lock.lock();
            try {
                return !(_size() > 0);
            } finally {
                lock.unlock();
            }
        }

        public void join() throws InterruptedException {
            while (notFinishedSize() > 0 || getSize() > 0) {
                lock.lock();
                allTaskDone.await();
                lock.unlock();
            }
        }

        /**
         * 未完成的任务加上已完成的任务数量
         * @return int
         */
        public int notFinishedSize() {
            lock.lock();
            try {
                return unfinished;
            } finally {
                lock.unlock();
            }
        }

        public int getMaxSize() {
            lock.lock();
            try {
                return size;
            } finally {
                lock.unlock();
            }
        }

        public int getSize() {
            lock.lock();
            try {
                return _size();
            } finally {
                lock.unlock();
            }
        }

        // 与同步队列实现无关的技术细节
        private void _init(int size) {
            pool = new ArrayDeque<>(size);
        }

        private void _add(E e) {
            pool.add(e);
        }

        private E _get() {
            return pool.poll();
        }

        private int _size() {
            return pool.size();
        }
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
                            System.out.println("Add Job, Job pool size now is: " + notFinishedSize());

                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }

        public void initHandler(CustomParser parser, HTMLParse htmlParser) {
            System.out.println("Setting handler size is " + parsers + ".");
            handlers = new Thread[parsers];

            for (int i = 0; i < parsers; i++) {
                handlers[i] = new Thread(new Runnable() {
                    Handler handler = new Handler(htmlParser);

                    @Override
                    public void run() {
                        Response response;
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
                                    } catch (ProtocolException | MalformedURLException e) {
                                        e.printStackTrace();
                                        System.out.println(parseResponse.getUrl());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        } catch (NoPathFoundException | InterruptedException e) {
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
            }

            for (Thread handler : handlers) {
                handler.start();
            }

            join();
        }
    }
}