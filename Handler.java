package crawler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import crawler.Consumer.Storeable;
import crawler.Consumer.Parserable;
import crawler.http.Response;

public class Handler extends Consumer<InputStream> implements HttpParserable, Storeable {
    private Response response;

    public Handler(Response response) {
        super(response.getInputStream());
    }

    @Override
    public void parse() {
        if (response.getContentType().indexOf("image") > 0) {
            // todo: 不知道是否能从对象内部获取到外部的变量
            storeAsFile(storePath);
        } else {
            storeInDB();
        }
    }

    public void store() {

    }

    @Override
    public void storeInDB() {

    }

    @Override
    public void storeAsFile(String storePath) {
        try (
            RandomAccessFile file = new RandomAccessFile(storePath, "rw");
        ){
            byte buffer = new byte[bufferSize];
            int hasRead = 0;
            while (response.getFileLength() < response.getContentLength() && (hasRead = super.getInput().read(buffer)) != -1) {
                file.write(buffer);
                length += hasRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            super.getInput().close();
        }
    } 
}