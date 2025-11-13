public class App {
    public static void main(String[] args) throws Exception {
        
        DBConnection dbConnection = DBConnection.getInstance();

        if (dbConnection.isConnected()) {
            System.out.println("Database connection established successfully.");
        } else {
            System.out.println("Failed to establish database connection.");
        }
    }
}
