package crawler.models;

abstract class Consumer <E>{
    private E input;

    public Consumer(E input) {
        this.input = input;
    }

    public E getInput() {
        return input;
    }
}