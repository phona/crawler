package crawler.pools;

import java.util.ArrayDeque;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Pool <E> {
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
        try {
            if (size > unfinished) {
                _add(e);
                unfinished++;
            } else {
                notFull.signal();
                while (unfinished > 0) notEmpty.await();
            }
            notFull.signal();
        } finally {
            lock.unlock();
        }
    }

    // not full
    public synchronized E get() throws InterruptedException {
        lock.lock();
        try {
            E item;
            if (getSize() > 0) {
                item = _get();
            } else {
                while (!(_size() > 0)) notFull.await();
                System.out.println(321);
                item = _get();
            }
            notEmpty.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    public void taskDone() {
        lock.lock();
        try {
            int unfinishedTask = notFinishedSize() - 1;
            if (unfinishedTask <= 0) {
                if (unfinishedTask < 0) throw new IndexOutOfBoundsException("Too many taskDone was called.");
            }
            this.unfinished = unfinishedTask;
            System.out.println("Consume a task. Left tasks: " + unfinished);
            allTaskDone.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return size <= _size();
        } finally {
            lock.unlock();
        }
    }

    public boolean hasItems() {
        lock.lock();
        try {
            return _size() > 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return _size() == 0;
        } finally {
            lock.unlock();
        }
    }

    public void join() throws InterruptedException {
        lock.lock();
        while (unfinished > 0) {
            System.out.println("NotFinishedSize: " + notFinishedSize() + " Size: " + getSize());
            allTaskDone.await();
        }
        lock.unlock();
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

    int getMaxSize() {
        return size;
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