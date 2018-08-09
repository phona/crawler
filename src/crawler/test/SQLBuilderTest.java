package crawler.test;

import crawler.db.SQLBuilder;

public class SQLBuilderTest {
    public static void main(String[] args) {
        String result = SQLBuilder.select()
                                  .from("main_channel")
                                  .group("name")
                                  .getSQL();
        String result1 = SQLBuilder.insert("main_channel")
                                   .values("name", "golang")
                                   .values("title", "xx")
                                   .values("age", 18)
                                   .getSQL();
        System.out.println(result);
        System.out.println(result1);
    }
}
