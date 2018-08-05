package crawler;

import java.util.ArrayList;

public class Util {
    public static class URLPool{
        private ArrayList<String> arr;
        private int size;

        public URLPool(int size) {
            this.arr = new ArrayList<>(size); 
            this.size = size;
        }

        public synchronized String get() {
            try {
                return arr.remove(0);
            } catch (IndexOutOfBoundsException ex) {
                return null;
            }
        }

        public synchronized void push(String url) throws PoolOverFlowException {
            if (arr.size() < size) {
                arr.add(url); 
            } else {
                throw new PoolOverFlowException("The pool is full, can push item anymore.");
            }
        }
    }

    public static class PoolOverFlowException extends Exception {
        private static final long serialVersionUID = 1L;

        public PoolOverFlowException(String msg) {
            super(msg);
        }

        public PoolOverFlowException() {}

        public PoolOverFlowException(Throwable t) {
            super(t);
        }
    }

    public static class InvalidURLException extends Exception {
        private static final long serialVersionUID = 1L;

        public InvalidURLException() {}

        public InvalidURLException(Throwable t) {
            super(t);
        }
    }
}