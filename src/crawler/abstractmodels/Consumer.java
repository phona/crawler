package crawler.abstractmodels;

public abstract class Consumer <E>{
    public abstract void setItem(E e);

    public abstract void handleItem() throws Exception;
}