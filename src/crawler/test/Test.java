package crawler.test;

import java.util.Collection;
import java.util.LinkedList;

public class Test {
    public static void main(String[] args) {
        B b = new B();
        System.out.println(b.get());
    }

}

class A {
    private int a = 1;

    public int get() {
        return a;
    }
}

class B extends A {

}