import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CreateTables extends JPanel {
    private final DBConnection dbConnection; 
    
    public CreateTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Create Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JButton executeButton = new JButton("Create Tables (Default)");
        executeButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        executeButton.addActionListener(e -> {
            boolean allSuccessful = true;
            StringBuilder errors = new StringBuilder();

            try {
                // Get the current connection object from dbConnection
                Connection connection = dbConnection.getConnection();

                try {
                    // Table creation statements
                    dbConnection.executeUpdate("CREATE TABLE Company (" 
                        + "companyID     INTEGER PRIMARY KEY, "
                        + "name          VARCHAR(30) NOT NULL UNIQUE, "
                        + "industry      VARCHAR(100), "
                        + "location      VARCHAR(200), "
                        + "email         VARCHAR(100), "
                        + "phone         VARCHAR(20) "
                        + ")");
                    dbConnection.executeUpdate("CREATE TABLE Recruiter (" 
                        + "recruiterID   INTEGER PRIMARY KEY, "
                        + "companyID     INTEGER NOT NULL, "
                        + "first_name    VARCHAR(30), "
                        + "last_name     VARCHAR(30), "
                        + "email         VARCHAR(100), "
                        + "phone         VARCHAR(20), "
                        + "FOREIGN KEY (companyID) REFERENCES Company (companyID) "
                        + ")");
                    dbConnection.executeUpdate("CREATE TABLE Job (" 
                        + "jobID         INTEGER PRIMARY KEY, "
                        + "companyID     INTEGER NOT NULL, "
                        + "recruiterID   INTEGER NOT NULL, "
                        + "salary        FLOAT, "
                        + "workingHours  FLOAT, "
                        + "datePosted    DATE, "
                        + "location      VARCHAR(200), "
                        + "title         VARCHAR(100) NOT NULL, "
                        + "description   VARCHAR(500) NOT NULL, "
                        + "FOREIGN KEY (companyID) REFERENCES Company (companyID), "
                        + "FOREIGN KEY (recruiterID) REFERENCES Recruiter (recruiterID) "
                        + ")");
                    dbConnection.executeUpdate("CREATE TABLE JobApplicant (" 
                        + "applicantID   INTEGER PRIMARY KEY, "
                        + "first_name    VARCHAR(30) NOT NULL, "
                        + "last_name     VARCHAR(30), "
                        + "industry      VARCHAR(100), "
                        + "birthdate     DATE, "
                        + "address       VARCHAR(200), "
                        + "email         VARCHAR(100), "
                        + "phone         VARCHAR(20) "
                        + ")");
                    dbConnection.executeUpdate("CREATE TABLE JobApplication (" 
                        + "jobAppID      INTEGER PRIMARY KEY, "
                        + "jobID         INTEGER NOT NULL, "
                        + "applicantID   INTEGER NOT NULL, "
                        + "dateTime      DATE, "
                        + "status        VARCHAR(20), "
                        + "FOREIGN KEY (jobID) REFERENCES Job (jobID), "
                        + "FOREIGN KEY (applicantID) REFERENCES JobApplicant (applicantID)"
                        + ")");
                    dbConnection.executeUpdate("CREATE TABLE Resume (" 
                        + "resumeID      INTEGER PRIMARY KEY, "
                        + "applicantID   INTEGER NOT NULL, "
                        + "upload_file   BLOB NOT NULL, "
                        + "uploadDate    DATE DEFAULT SYSDATE NOT NULL, "
                        + "FOREIGN KEY (applicantID) REFERENCES JobApplicant (applicantID)"
                        + ")");
                    dbConnection.executeUpdate("CREATE TABLE Interview (" 
                        + "interviewID   INTEGER PRIMARY KEY, "
                        + "jobAppID      INTEGER NOT NULL, "
                        + "dateTime      DATE DEFAULT SYSDATE NOT NULL, "
                        + "location      VARCHAR(100) NOT NULL, "
                        + "FOREIGN KEY (jobAppID) REFERENCES JobApplication (jobAppID) "
                        + ")");
                    
                    // Commit the transaction if all tables are created successfully
                    connection.commit();
                    JOptionPane.showMessageDialog(this, "Tables created successfully!");

                } catch (SQLException ex) {
                    allSuccessful = false;
                    connection.rollback(); // Rollback the transaction in case of error
                    errors.append("Failed to create table: ").append(ex.getMessage()).append("\n");
                    JOptionPane.showMessageDialog(this, "Some tables could not be created:\n" + errors, "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(), "Database Connection Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(executeButton, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());
        add(backButton, BorderLayout.SOUTH);
    }
}