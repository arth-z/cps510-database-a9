import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CreateTables extends JPanel {
    private final DBConnection dbConnection; 
    private final JTextArea outputArea = new JTextArea();
    
    public CreateTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Create Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("SQL Output"));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton executeButton = new JButton("Create Tables (Default)");
        executeButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        executeButton.addActionListener(e -> createTables());

        JButton viewOutputButton = new JButton("View Output");
        viewOutputButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        viewOutputButton.addActionListener(e -> gui.addOutputPanel("CreateTablesOutput", outputArea));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());

        buttonPanel.add(executeButton);
        buttonPanel.add(viewOutputButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void createTables() {
        outputArea.setText(""); 
        log("Starting table creation process...\n");

        try {
            /* Get the current connection object from dbConnection */
            Connection connection = dbConnection.getConnection();
            connection.setAutoCommit(false);

            try {
                /* Table creation statements */
                executeAndLog("CREATE TABLE Company (" 
                    + "companyID     INTEGER PRIMARY KEY, "
                    + "name          VARCHAR(30) NOT NULL UNIQUE, "
                    + "industry      VARCHAR(100), "
                    + "location      VARCHAR(200), "
                    + "email         VARCHAR(100), "
                    + "phone         VARCHAR(20) "
                    + ")", "Table 'Company' created successfully.");
                executeAndLog("CREATE TABLE Recruiter (" 
                    + "recruiterID   INTEGER PRIMARY KEY, "
                    + "companyID     INTEGER NOT NULL, "
                    + "first_name    VARCHAR(30), "
                    + "last_name     VARCHAR(30), "
                    + "email         VARCHAR(100), "
                    + "phone         VARCHAR(20), "
                    + "FOREIGN KEY (companyID) REFERENCES Company (companyID) "
                    + ")", "Table 'Recruiter' created successfully.");
                executeAndLog("CREATE TABLE Job (" 
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
                    + ")", "Table 'Job' created successfully.");
                executeAndLog("CREATE TABLE JobApplicant (" 
                    + "applicantID   INTEGER PRIMARY KEY, "
                    + "first_name    VARCHAR(30) NOT NULL, "
                    + "last_name     VARCHAR(30), "
                    + "industry      VARCHAR(100), "
                    + "birthdate     DATE, "
                    + "address       VARCHAR(200), "
                    + "email         VARCHAR(100), "
                    + "phone         VARCHAR(20) "
                    + ")", "Table 'JobApplicant' created successfully.");
                executeAndLog("CREATE TABLE JobApplication (" 
                    + "jobAppID      INTEGER PRIMARY KEY, "
                    + "jobID         INTEGER NOT NULL, "
                    + "applicantID   INTEGER NOT NULL, "
                    + "dateTime      DATE, "
                    + "status        VARCHAR(20), "
                    + "FOREIGN KEY (jobID) REFERENCES Job (jobID), "
                    + "FOREIGN KEY (applicantID) REFERENCES JobApplicant (applicantID)"
                    + ")", "Table 'JobApplication' created successfully.");
                executeAndLog("CREATE TABLE Resume (" 
                    + "resumeID      INTEGER PRIMARY KEY, "
                    + "applicantID   INTEGER NOT NULL, "
                    + "upload_file   BLOB NOT NULL, "
                    + "uploadDate    DATE DEFAULT SYSDATE NOT NULL, "
                    + "FOREIGN KEY (applicantID) REFERENCES JobApplicant (applicantID)"
                    + ")", "Table 'Resume' created successfully.");
                executeAndLog("CREATE TABLE Interview (" 
                    + "interviewID   INTEGER PRIMARY KEY, "
                    + "jobAppID      INTEGER NOT NULL, "
                    + "dateTime      DATE DEFAULT SYSDATE NOT NULL, "
                    + "location      VARCHAR(100) NOT NULL, "
                    + "FOREIGN KEY (jobAppID) REFERENCES JobApplication (jobAppID) "
                    + ")", "Table 'Interview' created successfully.");
                
                connection.commit();
                log("\nAll tables created successfully!");
                JOptionPane.showMessageDialog(this, "Tables created successfully!");

            } catch (SQLException ex) {
                connection.rollback();
                log("\nRolled back changes due to error:");
                log(ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error creating tables. Rolled back changes.\n" + ex.getMessage(),"SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            log("\nDatabase connection error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(),"Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Helper for executing and logging SQL statements */
    private void executeAndLog(String sql, String successMessage) {
        try {
            dbConnection.executeUpdate(sql);
            log(successMessage);
        } catch (SQLException ex) {
            log("ERROR executing: " + sql);
            log("→ " + ex.getMessage());
        }
    }

    /* Helper to append text to output area */
    private void log(String message) {
        outputArea.append(message + "\n");
    }
    
}