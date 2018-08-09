package crawler.abstractmodels;

public abstract class Consumer <E>{
    public abstract void addItem(E e);

    public abstract void handleItem() throws Exception;
}