package crawler.test;

import java.util.Base64;

public class DecoderTest {
    public static void main(String[] args) {
        String test = "Ly93eDQuc2luYWltZy5jbi9tdzYwMC8wMDc2QlNTNWx5MWZ1NXozam00ZW1qMzFrdzExend5aS5qcGc=";

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] resultByte = decoder.decode(test);
        System.out.println(new String(resultByte));
    }
}
