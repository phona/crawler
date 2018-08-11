package crawler.impl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import static crawler.Settings.imgPath;
import static crawler.abstractmodels.CustomInterface.Store;
import static crawler.abstractmodels.CustomInterface.HttpParserable;

import crawler.abstractmodels.Consumer;
import crawler.abstractmodels.CustomInterface.HTMLParse;
import crawler.db.SQLBuilder;
import crawler.util.CustomExceptions.NoPathFoundException;
import crawler.http.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Handler extends Consumer<Response> implements HttpParserable, Store {
    private Response response = null;
    private String path;
    private int length;
    private HTMLParse parser = null;

    {
        this.setPath(imgPath);
    }

    public Handler(HTMLParse parser) {
        this.parser = parser;
    }

    public Handler() {}

    public void setHTMLParse(HTMLParse p) {
        parser = p;
    }

    @Override
    public void setItem(Response response) {
        this.response = response;
    }

    @Override
    public void handleItem() throws NoPathFoundException {
        if (path == null) {
            throw new NoPathFoundException(); 
        }

        if (response.getContentType().contains("image")) {
            String fileType = response.getUrl().substring(response.getUrl().lastIndexOf("."));
            // todo: 不知道是否能从对象内部获取到外部的变量
            // System.out.println(fileType);
            storeAsFile(path + "/" + encrypt(response.getUrl()) + fileType);
        } else {
            storeInDB(response);
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public void store() {

    }

    public void storeInDB(Response response) {
        try {
            Document doc = Jsoup.parse(response.getInputStream(), "UTF-8", response.getUrl());
            parser.parse(doc);
            System.out.println(doc.title());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(SQLBuilder.select().from("main_channel").getSQL());
    }

    @Override
    public void storeAsFile(String storePath) {
        try (
            RandomAccessFile file = new RandomAccessFile(storePath, "rw");
        ){
            file.setLength(response.getContentLength());
            byte[] buffer = new byte[bufferSize];
            int hasRead = 0;
            while ((hasRead = response.getInputStream().read(buffer)) != -1) {
                file.write(buffer, 0, hasRead);
                this.length += hasRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.length = 0;
            try {
                response.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    } 

    private static String encrypt(String str) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}