package crawler.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayDeque;
import java.util.Collection;

public class DB <E> {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private ArrayDeque<String> querys = new ArrayDeque<>();
    private DataSource ds;

    public DB(DataSource ds) throws SQLException {
        this.ds = ds;
    }

    public void query(String query) {
        querys.add(query);
    }

    /**
     *
     * @param collection 如果不需要可以传一个null
     * @param dataParse
     * @param <E>
     */
    public void commit(Object store, crawler.db.DataParse dataParse) {
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
            stmt = conn.createStatement();

            // 处理多个SQL语句
            // 可以是插入、删除或更新，或者控制性语句
            boolean batch = false;
            for (int i = 0; i < querys.size()-1; i++) {
                stmt.addBatch(querys.pollFirst());
                if (i == querys.size() - 1) batch = true;
            }
            if (batch) stmt.executeBatch();
            conn.commit();

            // 最后一句一定是查询语句
            rs = stmt.executeQuery(querys.pollFirst());
            dataParse.parse(rs);
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {
            try {
                if (conn != null) conn.close();
                if (stmt != null) stmt.close();
                if (rs != null) rs.close();
            } catch (SQLException ex) {
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            } finally {
                conn = null;
                stmt = null;
                rs = null;
                querys.clear();
            }
        }
    }
}
