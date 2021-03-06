package crawler.abstractmodels;

import crawler.http.Request;
import crawler.http.Response;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Collection;

public class CustomInterface {
    public abstract interface HttpRequestable {
        public void get(Request req, int timeout) throws IOException;
    
        public void post(Request req, int timeout) throws IOException;
    }

    public static abstract interface HttpParserable {
        public int bufferSize = 1024;
    }

    @FunctionalInterface
    public static interface HTMLParse {
        void parse(Response response, Document doc);
    }

    public static interface Store {
        void store();

        void storeAsFile(String storePath);
    }

    public static interface CustomParser {
        Response run(Response e);
    }
}