package crawler.models;

import static crawler.Utils.Pools.Pool;

public abstract class Producer <E> {
    
    public abstract E toConsume();
}