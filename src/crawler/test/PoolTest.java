package crawler.test;

public class PoolTest {
    public static void main(String[] args) throws Exception {
        Pool<Integer> pool = new Pool<>(10);

        for (int i = 0; i < 5; i++) {
            new Thread(()->{
                for (int j = 0; j < 20; j++) {
                    try {
                        pool.add(j);
                        System.out.println(Thread.currentThread().getName() + " add " + j + " to the pool");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        for (int i = 0; i < 1; i++) {
            new Thread(()->{
                while (!pool.isEmpty()) {
                    try {
                        int item = pool.get();
                        System.out.println(Thread.currentThread().getName() + " get " + item + " from the pool");
                        pool.taskDone();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("done");
            }).start();
        }

        pool.join();
    }
}
