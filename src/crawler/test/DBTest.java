package crawler.test;

import com.mysql.cj.jdbc.MysqlDataSource;
import crawler.db.DB;
import crawler.db.SQLBuilder;
import java.util.ArrayList;

public class DBTest {

    public static void main(String[] args) throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("123456");
        dataSource.setServerName("localhost");

        DB db = new DB(dataSource);
        db.query(SQLBuilder.query("USE ubox_english_sz_tw"));
        db.query(SQLBuilder.select().from("main_channel").getSQL());

        ArrayList<String> arr = new ArrayList<>();

        db.commit(arr, (rs) -> {
            rs.first();
            System.out.println(rs.getString("c_name"));
            arr.add(rs.getString("c_name"));
        });

        System.out.println(arr);


//        MysqlDataSource dataSource = new MysqlDataSource();
//        dataSource.setUser("root");
//        dataSource.setPassword("123456");
//        dataSource.setServerName("localhost");
//
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//
//        try {
//            conn = dataSource.getConnection();
//            stmt = conn.createStatement();
//            stmt.executeQuery("USE ubox_flower_itv;");
//            rs = stmt.executeQuery("SELECT * FROM main_channel;");
//            System.out.println(rs);
//
//        } catch (SQLException ex) {
//            System.out.println("SQLException: " + ex.getMessage());
//            System.out.println("SQLState: " + ex.getSQLState());
//            System.out.println("VendorError: " + ex.getErrorCode());
//        } finally {
//            try {
//                if (conn != null) conn.close();
//                if (stmt != null) stmt.close();
//                if (rs != null) rs.close();
//            } catch (SQLException ex) {
//                System.out.println("SQLException: " + ex.getMessage());
//                System.out.println("SQLState: " + ex.getSQLState());
//                System.out.println("VendorError: " + ex.getErrorCode());
//            }
//        }
    }
}
