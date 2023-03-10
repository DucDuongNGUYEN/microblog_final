import java.sql.*;

public class MicroblogDatabase {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost/db_microblog";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection conn;

    public MicroblogDatabase() throws SQLException {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to db_microblog");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void insertUser(String username, String password) throws SQLException {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        pstmt.executeUpdate();
        System.out.println("User inserted: " + username);
    }

    public void insertMessage(String username, String content) throws SQLException {
        String sql = "INSERT INTO messages (username, content) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, content);
        pstmt.executeUpdate();
        System.out.println("Message inserted: " + content);
    }

    public void selectAllMessages() throws SQLException {
        String sql = "SELECT * FROM messages";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            int id = rs.getInt("id");
            String username = rs.getString("username");
            String content = rs.getString("content");
            Timestamp timestamp = rs.getTimestamp("timestamp");
            System.out.println(id + "\t" + username + "\t" + content + "\t" + timestamp);
        }
    }

    public void close() throws SQLException {
        conn.close();
        System.out.println("Connection closed");
    }

    public static void main(String[] args) {
        try {
            MicroblogDatabase db = new MicroblogDatabase();
            db.insertUser("john", "password123");
            db.insertMessage("john", "Hello World!");
            db.selectAllMessages();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
