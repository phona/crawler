package crawler.impl;

import crawler.http.Request;
import crawler.http.Response;
import crawler.pools.Pool;
import crawler.pools.RequestPool;
import crawler.pools.ResponsePool;
import crawler.pools.ThreadPool;

import javax.xml.crypto.Data;

import static crawler.Settings.*;
import static crawler.util.CustomExceptions.NoPathFoundException;
import static crawler.abstractmodels.CustomInterface.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * 这里就提供业务代码
 */
public class TaskPool extends ThreadPool {
    private RequestPool requestPool;
    private ResponsePool responsePool;
    private Runnable handle;
    private Runnable send;

    public TaskPool(int size) {
        super(size);
    }

    public void initialize(RequestPool requestPool, ResponsePool responsePool) {
        this.requestPool = requestPool;
        this.responsePool = responsePool;
        initSenders();
    }

    public void testGo() {
        for (int i = 0; i < requestPool.getSize(); i++) {
            applyForAnAction("sender");
        }
    }

    public void go() {
        while (true) {
            if (requestPool.isFull()) {
//                System.out.println("is Full");
                execute(handle);
            } else {
//                System.out.println("is Empty");
                execute(send);
            }
        }
    }

    private void applyForAnAction(String action) {
        execute(action.toLowerCase() == "handle" ? handle : send);
    }

    private void initSenders() {
        send = () -> {
            // getRequest与get(int)两个方法会抛出相同的异常
            try {
                // 从请求池里获取一个请求进行处理
                Request request = requestPool.get();

                System.out.println(Thread.currentThread().getName()
                        + " is construct request: "
                        + request.getURL());

                // 发起请求
                Sender reqSender = new Sender(request, timeout);
                Response response = reqSender.get();

                System.out.println(Thread.currentThread().getName() + " received response.");

                // 将响应加入响应池中
                responsePool.add(response);
                applyForAnAction("handle");
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        };
    }

    public void initHandler(CustomParser parser, HTMLParse htmlParser) {

        handle = () -> {
            try {
                Handler handler = new Handler(htmlParser);
                Response response = responsePool.get();

                System.out.println(Thread.currentThread().getName() + " is consume: " + response.getUrl());

                // 处理请求
                Response parseResponse = parser.run(response);
                handler.setItem(parseResponse);
                handler.handleItem();

                // 处理META中的数据
                ArrayList<String> urls = parseResponse.getMeta().getUrls();

                if (parseResponse.getMeta().getUrls() != null) {
                    urls.forEach(url-> {
                        try {
                            // 因为请求池的大小有限制，所以无法写入所有请求，就会导致死锁
                            requestPool.add(new Request(url));
                            applyForAnAction("send");
                        } catch (ProtocolException | MalformedURLException e) {
                            e.printStackTrace();
                            System.out.println(parseResponse.getUrl());
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
                System.out.println(Thread.currentThread().getName() + " handle complete: " + response.getUrl());
            } catch (NoPathFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        };
    }
}
