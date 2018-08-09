package crawler.test;

import java.util.Collection;
import java.util.LinkedList;

public class Test {
    public static void main(String[] args) {
        LinkedList a = new LinkedList();
        testType(a);
    }

    public static void testType(Collection c) {
        Class clazz = c.getClass();
        System.out.println(clazz);
    }
}