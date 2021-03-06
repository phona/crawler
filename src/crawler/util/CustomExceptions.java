package crawler.util;

public class CustomExceptions {
    /**
     * 自定义的异常类
     * @category Exception
     */
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

    public static class PoolNotSufficientException extends Exception {
        private static final long serialVersionUID = 2L;

        public PoolNotSufficientException(String msg) {
            super(msg);
        }

        public PoolNotSufficientException() {}

        public PoolNotSufficientException(Throwable t) {
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

    public static class NoPathFoundException extends Exception {
        private static final long serialVersionUID = 1L;

        public NoPathFoundException() {}

        public NoPathFoundException(Throwable t) {
            super(t);
        }
    }
}