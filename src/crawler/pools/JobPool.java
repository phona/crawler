package crawler.pools;

import crawler.http.Request;
import crawler.http.Response;
import crawler.impl.Sender;
import crawler.impl.Handler;
import static crawler.Settings.requests;
import static crawler.Settings.timeout;
import static crawler.Settings.parsers;
import static crawler.util.CustomExceptions.NoPathFoundException;
import static crawler.abstractmodels.CustomInterface.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;

/**
 * 用于生产者与消费者之间的通信
 */
public class JobPool extends Pool<Response> {
    private RequestPool requestPool;
    private Thread[] senders;
    private Thread[] handlers;

    /**
     * 设置job队列上限
     * @param size int
     */
    public JobPool(int size) {
        super(size);
    }

    public void initialize(RequestPool requestPool) {
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
                    while (!requestPool.isEmpty()) {
                        // System.out.println("Get size from sender: " + requestPool.getSize());
                        try {
                            // getRequest与get(int)两个方法会抛出相同的异常
                            Request request = sender.getRequest();
                            System.out.println(
                                    Thread.currentThread().getName()
                                            + " is construct request: "
                                            + request.getURL());

                            Response response = sender.get(timeout);
                            System.out.println(
                                    Thread.currentThread().getName()
                                            + " receive a response from: "
                                            + request.getURL());

                            add(response);
                            System.out.println("Add Job, Job pool size now is: " + notFinishedSize());

                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
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
                    while (!requestPool.isEmpty()) {
                        try {
                            response = get();
                            System.out.println(Thread.currentThread().getName() + " is consume: " + response.getUrl());
                            Response parseResponse = parser.run(response);
                            handler.setItem(parseResponse);
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
                        } finally {
                            requestPool.taskDone();
                        }
                    }
                    System.out.println("Handler done.");
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

        requestPool.join();
    }
}

