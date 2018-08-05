package crawler.models;

import crawler.Utils.CustomExceptions.NoPathFoundException;
import crawler.http.Request;

import java.io.IOException;

public class CustomInterface {
    public abstract interface HttpRequestable {
        public void get(Request req, int timeout) throws IOException;
    
        public void post(Request req, int timeout) throws IOException;
    }

    @FunctionalInterface
    public static abstract interface HttpParserable {
        public int bufferSize = 1024;

        public abstract void parse() throws NoPathFoundException;
    }

    public static abstract interface Storeable {
        public abstract void store();

        public abstract void storeInDB();

        public abstract void storeAsFile(String storePath);
    }
}