package crawler.db;

import crawler.util.Utils;

import static crawler.util.Utils.Adaptor;

import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 构造SQL，返回SQL
 */
public class SQL {
    private ArrayDeque<SQL> sqls = new ArrayDeque<>();

    public SQL(LinkedHashMap<String, Adaptor> map) {

    }

    public void extend(SQL sql) {
        sqls.add(sql);
    }

    public void extend(List<SQL> sqls) {
        this.sqls.addAll(sqls);
    }

    public SQL pop() {
        return sqls.pop();
    }

    public String toString() {
        String result = "";

        return result;
    }
}
