package crawler.db;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface DataParse {
    void parse(ResultSet rs) throws SQLException;
}
