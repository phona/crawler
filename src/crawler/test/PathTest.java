package crawler.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathTest {
    public static void main(String[] args) throws IOException {
        Path o = Paths.get(System.getProperty("user.dir"), "images");
        System.out.println(o.toAbsolutePath());

//        try (
//                RandomAccessFile r = new RandomAccessFile(o.toAbsolutePath().toString(), "rw");
//                ) {
//            r.write("Hello, world".getBytes());
//        }
    }
}
