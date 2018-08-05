package crawler.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import static crawler.models.CustomInterface.Storeable;
import static crawler.models.CustomInterface.HttpParserable;
import crawler.models.Consumer;
import crawler.Utils.CustomExceptions.NoPathFoundException;
import crawler.http.Response;

public class Handler extends Consumer<Response> implements HttpParserable, Storeable {
    private Response response;
    private String path;
    private int length;

    public Handler(Response response) {
        this.response = response;
    }

    public Handler() {}

    @Override
    public void parse() throws NoPathFoundException {
        if (path == null) {
            throw new NoPathFoundException(); 
        }

        if (response.getContentType().indexOf("image") >= 0) {
            String fileType = response.getUrl().substring(response.getUrl().lastIndexOf("."));
            // todo: 不知道是否能从对象内部获取到外部的变量
            // System.out.println(fileType);
            storeAsFile(path + "/" + encrypt(response.getUrl()) + fileType);
        } else {
            storeInDB();
        }
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
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
            file.setLength(response.getContentLength());
            byte[] buffer = new byte[bufferSize];
            int hasRead = 0;
            while ((hasRead = response.getInputStream().read(buffer)) != -1) {
                file.write(buffer, 0, hasRead);
                length += hasRead;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            length = 0;
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
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