import java.sql.*; 

/* Manages connection to sql database and executes queries */
public class DBConnection {

    /* Credentials to log into Oracle database */
    private final String url = "jdbc:oracle:thin:@oracle.cs.torontomu.ca:1521:orcl";
    private String username;
    private String password;

    /* Create an instance of DBConnection */
    private static DBConnection connectionInstance; 

    /* Create a persistent connection object */
    private Connection connection;

    /* Constructor to prevent direct instantiation */
    private DBConnection(String username, String password) throws SQLException {
        this.username = username;
        this.password = password;   

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.connection = DriverManager.getConnection(url, username, password);
            this.connection.setAutoCommit(false);
            System.out.println("Database connection established.");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("Oracle JDBC Driver not found.");
        }
    }

    /* Gets instance of the database connection */
    public static DBConnection getInstance(String username, String password) throws SQLException {
        if (connectionInstance == null) {
            connectionInstance = new DBConnection(username, password);
        } 
        return connectionInstance;
    }

    public Connection getConnection() {
        return this.connection;
    }

    /* Verifies if connection is established */
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* Execute DDL/DML statements */
    public void executeUpdate(String query) throws SQLException {
        try (Statement statement = this.connection.createStatement()) {
            statement.executeUpdate(query);
            this.connection.commit();
        } catch (SQLException e) {
            this.connection.rollback();
            throw e;
        }
    }

    /* Execute SELECT query and return the ResultSet */
    public ResultSet executeQuery(String query) throws SQLException {
        Statement statement = this.connection.createStatement();
        return statement.executeQuery(query);
    }

    /* Commit the actions */
    public void commit() throws SQLException {
        if (this.connection != null) { 
            this.connection.commit();
        }
    }

    /* Rollback the actions */
    public void rollback() throws SQLException {
        if (this.connection != null) { 
            this.connection.rollback();
        }
    }

    /* Close database connection */
    public void close() throws SQLException {
        try { 
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
}
