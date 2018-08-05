package crawler.models;

public abstract class Consumer <E>{
    private E input;

    public Consumer() {}

    public Consumer(E input) {
        this.input = input;
    }

    public E getInput() {
        return input;
    }
}