package crawler.util;

public class Utils {
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static class Adaptor<E> {
        private E obj;

        public Adaptor(E obj) {
            this.obj = obj;
        }

        public E getObj() {
            return obj;
        }
    }
}
