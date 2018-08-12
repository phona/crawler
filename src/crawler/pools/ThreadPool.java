package crawler.pools;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO:需要实现一个定时进程用于计算是否超时
 */
public class ThreadPool {
    private final int size;
    private final PoolWorker[] poolWorkers;
    private final LinkedBlockingQueue<Runnable> queue;
    private final Lock lock;
    private final Condition cond;

    public ThreadPool(int size) {
        this.size = size;
        queue = new LinkedBlockingQueue<>();
        poolWorkers = new PoolWorker[size];
        lock = new ReentrantLock();
        cond = lock.newCondition();

        for (int i = 0; i < poolWorkers.length; i++) {
            poolWorkers[i] = new PoolWorker();
            poolWorkers[i].start();
        }
    }

    public void execute(Runnable task) {
        lock.lock();
        queue.add(task);
        System.out.println("Thread pool size is: " + queue.size());
        cond.signal();
        lock.unlock();
    }

    private class PoolWorker extends Thread {
        @Override
        public void run() {
            Runnable task;

            while (true) {
                lock.lock();
                try {
                    while (queue.isEmpty()) {
                        try {
                            cond.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } finally {
                    lock.unlock();
                }

                task = queue.poll();

                try {
                    task.run();
                } catch (RuntimeException e) {
                    System.out.println("Thread pool is interrupted due to an issue: " + e.getMessage());
                }
            }
        }
    }
}