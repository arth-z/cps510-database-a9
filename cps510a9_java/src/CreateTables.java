import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/* This class handles the creation of all database tables for the application */
public class CreateTables extends JPanel {
    private final DBConnection dbConnection;  /* Handles connection to Oracle database */
    private final JTextArea outputArea = new JTextArea(); /* Displays SQL execution logs and results */
   
    /* Sets up UI layout, output display, and button controls. Accepts username/password for establishing DB connection */
    public CreateTables(DB_GUI gui, String username, String password) throws SQLException {
        /* Get an existing database connection instance (singleton pattern) */
        this.dbConnection = DBConnection.getInstance(username, password);

        /* Set layout for the panel */
        setLayout(new BorderLayout());

        /* Title label at the top */
        JLabel title = new JLabel("Create Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        /* Configure text area to display SQL execution output */
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("SQL Output"));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);

        /* Create button panel at the bottom for user actions */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        /* Button to execute table creation */
        JButton executeButton = new JButton("Create Tables");
        executeButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        executeButton.addActionListener(e -> createTables());

        /* Button to view SQL output in a separate panel */
        JButton viewOutputButton = new JButton("View Output");
        viewOutputButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        viewOutputButton.addActionListener(e -> gui.addOutputPanel("CreateTablesOutput", outputArea));

        /* Button to return to the main menu */
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());

        /* Add buttons to the button panel */
        buttonPanel.add(executeButton);
        buttonPanel.add(viewOutputButton);
        buttonPanel.add(backButton);

        /* Add the button panel to the bottom of the screen */
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /* Executes SQL commands to create all tables. Includes proper transaction handling (commit/rollback) and detailed logging for each operation */
    private void createTables() {
        /* Clear old output and start logging */
        outputArea.setText(""); 
        log("Starting table creation process...\n");    /* Enable manual transaction control */

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
                    + "uploadFile   BLOB NOT NULL, "
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
                
                /* If all statements succeed, commit changes */
                connection.commit();
                log("\nAll tables created successfully!");
                JOptionPane.showMessageDialog(this, "Tables created successfully!");

            /* If an error occurs, rollback all table creation */
            } catch (SQLException ex) {
                connection.rollback();
                log("\nRolled back changes due to error:");
                log(ex.getMessage());
                JOptionPane.showMessageDialog(this, "Error creating tables. Rolled back changes.\n" + ex.getMessage(),"SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        /* Handle general database connection issues */
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