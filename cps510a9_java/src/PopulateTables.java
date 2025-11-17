import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/* This class is responsible for populating the database tables of the application */
public class PopulateTables extends JPanel {
    private final DBConnection dbConnection;  /* Handles connection to Oracle database */
    private final JTextArea outputArea = new JTextArea(); /* Displays SQL output and logs */

    /* Sets up the title, layout, and three main buttons: Populate, View Output, and Back */
    public PopulateTables(DB_GUI gui, String username, String password) throws SQLException {
        /* Get database connection instance using user credentials */
        this.dbConnection = DBConnection.getInstance(username, password);
        setLayout(new BorderLayout());

         /* Title label displayed at the top */
        JLabel title = new JLabel("Populate Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

         /* Configure SQL output area for displaying execution results */
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("SQL Output"));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);

        /* Create a panel for buttons at the bottom */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        /* Button to execute the SQL population process */
        JButton executeButton = new JButton("Populate Tables");
        executeButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        executeButton.addActionListener(e -> populateTables());

        /* Button to open the output log in a new window */
        JButton viewOutputButton = new JButton("View Output");
        viewOutputButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        viewOutputButton.addActionListener(e -> gui.addOutputPanel("PopulateTablesOutput", outputArea));

         /* Button to navigate back to the main menu */
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());

        /* Add buttons to the button panel */
        buttonPanel.add(executeButton);
        buttonPanel.add(viewOutputButton);
        buttonPanel.add(backButton);

        /* Add button panel to the main panel */
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /* Main method to populate the database tables with sample data. Includes detailed transaction management and logs for all SQL statements */
    private void populateTables() {
        outputArea.setText("");
        log("Starting table population process...\n");

            try {
                /* Get the current connection object from dbConnection */ 
                Connection connection = dbConnection.getConnection();
                connection.setAutoCommit(false);

                try {
                    /* Insert statements */
                    executeAndLog("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (1, 'Apple Canada', 'Software', '120 Bremner Boulevard Suite 1600, Toronto, Ontario, M5J 0A8', '', '647-943-4400')", "1 row inserted."); 
                    executeAndLog("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (2, 'Royal Bank of Canada (RBC)', 'Banking', 'Toronto, Ontario, Canada', 'recruitment@rbc.com', '1-800-769-2511')", "1 row inserted."); 
                    executeAndLog("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (3, 'AMD', 'Technology', 'Markham, Ontario, Canada', '', '905-882-2600')", "1 row inserted."); 
                    executeAndLog("INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (4, 'SAMSUNG', 'Hardware', 'Vancouver, British Columbia', 'recruitment@samsung.com', '416-230-8121')", "1 row inserted.");

                    executeAndLog("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (1, 1, 'Jane', 'Doe', 'jane.doe@apple.com', '647-222-3333')", "1 row inserted.");
                    executeAndLog("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (2, 2, 'Bob', 'William', 'bob.william@rbc.com', '416-111-1111')", "1 row inserted."); 
                    executeAndLog("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (3, 3, 'John', 'Daniels', 'john.daniels@amd.com', '905-423-5678')", "1 row inserted."); 
                    executeAndLog("INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (4, 4, 'Jack', 'Jones', 'jack.jones@samsung.com', '647-333-4444')", "1 row inserted.");

                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (1, 1, 1, 31.50, 36.25, TO_DATE('2025-09-22', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Software Engineer', 'Develop and maintain Apple software products.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (2, 2, 2, 24.50, 35.00, TO_DATE('2025-09-24', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Financial Analyst', 'Analyze financial data and provide insights for RBC clients.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (3, 3, 3, 28.50, 40.00, TO_DATE('2025-09-26', 'YYYY-MM-DD'), 'Markham, Ontario, Canada', 'Systems Design Engineer', 'Responsible for designing, integrating, and validating complex hardware and software systems.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (4, 4, 4, 25, 42, DATE '2025-09-28', 'Vancouver, British Columbia, Canada', 'Software Developer', 'Develop and maintain applications for Samsung.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (5, 1, 1, 31.50, 36.25, TO_DATE('2025-09-22', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Mobile Developer', 'Develop and maintain Apple mobile products and services.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (6, 1, 1, 31.50, 36.25, TO_DATE('2025-10-04', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Janitor', 'Mop the floors and clean the bathrooms.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (7, 1, 1, 40.75, 40.00, TO_DATE('2025-10-10', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Data Engineer', 'Design and maintain Apple’s data pipelines.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (8, 1, 1, 29.00, 38.00, TO_DATE('2025-10-11', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'IT Support Specialist', 'Provide internal tech support for Apple employees.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (9, 2, 2, 22.00, 35.00, TO_DATE('2025-10-08', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Bank Teller', 'Assist clients with daily transactions.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (10, 2, 2, 31.50, 37.50, TO_DATE('2025-10-09', 'YYYY-MM-DD'), 'Toronto, Ontario, Canada', 'Data Analyst', 'Analyze customer trends to improve banking performance.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (11, 3, 3, 45.00, 40.00, TO_DATE('2025-10-06', 'YYYY-MM-DD'), 'Markham, Ontario, Canada', 'Hardware Engineer', 'Develop and test AMD hardware components.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (12, 3, 3, 35.00, 40.00, TO_DATE('2025-10-07', 'YYYY-MM-DD'), 'Markham, Ontario, Canada', 'Software Engineer', 'Develop internal tools and automation frameworks for AMD.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (13, 4, 4, 26.50, 40.00, TO_DATE('2025-10-05', 'YYYY-MM-DD'), 'Vancouver, British Columbia, Canada', 'QA Tester', 'Perform software and hardware quality assurance tests.')", "1 row inserted.");
                    executeAndLog("INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (14, 4, 4, 30.00, 40.00, TO_DATE('2025-10-06', 'YYYY-MM-DD'), 'Vancouver, British Columbia, Canada', 'UI/UX Designer', 'Design user interfaces for Samsung applications.')", "1 row inserted.");

                    executeAndLog("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (1, 'Alice', 'Bob', 'Technology', TO_DATE('2002-04-13', 'YYYY-MM-DD'), '123 Bay Street, Toronto, Ontario, Canada, M1B 2K3', 'alice.bob@gmail.com', '416-123-455')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (2, 'Jake', 'Blake', 'Technology', TO_DATE('2004-05-06', 'YYYY-MM-DD'), '456 Main Street, Markham, Ontario, Canada, L6H 1F3', 'jake.blake@hotmail.com', '647-444-1947')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (3, 'Griffin', 'Walker', 'Banking', TO_DATE('2003-12-25', 'YYYY-MM-DD'), '789 Bond Avenue, Ajax, Ontario, Canada, L0H 1H9', 'griffin.walker@outlook.com', '905-289-9876')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (4, 'Ed', 'Stephens', 'Software', TO_DATE('2004-01-25', 'YYYY-MM-DD'), '145 Bloor Avenue, Toronto, Ontario, Canada, M5B 3K9', 'ed.stephens@outlook.com', '905-444-2121')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (5, 'Joe', 'Random', 'Technology', TO_DATE('2001-07-29', 'YYYY-MM-DD'), '7622 Markham Road, Markham, Ontario, Canada, L6H 9A3', 'joe.random@gmail.com', '647-543-2211')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (6, 'Michael', 'Jordan', 'Hardware', TO_DATE('2002-11-09', 'YYYY-MM-DD'), '116 Bond Street, Hamilton, Ontario, Canada, LOP 1B9', 'michael.jordan@gmail.com', '416-989-7777')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthdate, address, email, phone) VALUES (7, 'Sam', 'Inactive', 'Technology', TO_DATE('2002-04-13', 'YYYY-MM-DD'), '123 Bay Street, Toronto, Ontario, Canada, M1B 2K3', 'sam.inactive@gmail.com', '416-123-8293')", "1 row inserted.");

                    executeAndLog("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (1, 1, 1, TO_DATE('2025-09-28', 'YYYY-MM-DD'), 'Rejected')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (2, 2, 2, TO_DATE('2025-09-29', 'YYYY-MM-DD'), 'Under Review')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (3, 3, 3, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Submitted')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (4, 1, 4, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Interview Pending')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (5, 3, 5, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Interview Pending')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (6, 4, 6, TO_DATE('2025-09-30', 'YYYY-MM-DD'), 'Interview Pending')", "1 row inserted.");
                    executeAndLog("INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (7, 4, 5, TO_DATE('2025-10-04', 'YYYY-MM-DD'), 'Interview Pending')", "1 row inserted.");

                    executeAndLog("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (1, 1, UTL_RAW.CAST_TO_RAW('Alice Bob'), TO_DATE('2002-04-13', 'YYYY-MM-DD'))", "1 row inserted.");
                    executeAndLog("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (2, 2, UTL_RAW.CAST_TO_RAW('Jake Blake Resume'), TO_DATE('2025-09-28', 'YYYY-MM-DD'))", "1 row inserted.");
                    executeAndLog("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (3, 3, UTL_RAW.CAST_TO_RAW('Griffin Walker Resume'), TO_DATE('2025-09-29', 'YYYY-MM-DD'))", "1 row inserted.");
                    executeAndLog("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (4, 4, UTL_RAW.CAST_TO_RAW('Ed Stephens Resume'), TO_DATE('2025-09-28', 'YYYY-MM-DD'))", "1 row inserted.");
                    executeAndLog("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (5, 5, UTL_RAW.CAST_TO_RAW('Joe Random Resume'), TO_DATE('2025-09-29', 'YYYY-MM-DD'))", "1 row inserted.");
                    executeAndLog("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (6, 6, UTL_RAW.CAST_TO_RAW('Michael Jordan Resume'), TO_DATE('2025-09-30', 'YYYY-MM-DD'))", "1 row inserted.");
                    executeAndLog("INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (7, 6, UTL_RAW.CAST_TO_RAW('Michael Jordan Resume 2'), TO_DATE('2025-10-04', 'YYYY-MM-DD'))", "1 row inserted.");
                    
                    executeAndLog("INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (1, 4, TO_DATE('2025-10-1 10:00 AM', 'YYYY-MM-DD HH:MI AM'), 'Toronto, Ontario, Canada')", "1 row inserted.");
                    executeAndLog("INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (2, 5, TO_DATE('2025-10-2 11:00 AM', 'YYYY-MM-DD HH:MI AM'), 'Markham, Ontario, Canada')", "1 row inserted.");
                    executeAndLog("INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (3, 6, TO_DATE('2025-10-3 1:00 PM', 'YYYY-MM-DD HH:MI PM'), 'Vancouver, British Columbia, Canada')", "1 row inserted.");

                    /* Update statements */
                    executeAndLog("UPDATE Company SET location = 'Toronto, Ontario, Canada' WHERE companyID = 1", "1 row updated.");
                    executeAndLog("UPDATE Company SET industry = 'Technology' WHERE companyID = 1", "1 row updated.");
                    executeAndLog("UPDATE Resume SET uploadDate = TO_DATE('2025-09-27', 'YYYY-MM-DD') WHERE resumeID = 1", "1 row updated.");
                    executeAndLog("UPDATE Resume SET uploadFile = UTL_RAW.CAST_TO_RAW('Alice Bob Resume') WHERE resumeID = 1", "1 row updated.");

                    /* Commit the transaction after all insertions and updates succeed */
                    connection.commit(); 
                    log("\nTables populated successfully!");
                    JOptionPane.showMessageDialog(this, "Tables populated successfully!");
                /* Rollback the transaction if any SQL error occurs */
                } catch (SQLException ex) {
                    connection.rollback();
                    log("\nRolled back changes due to error:");
                    log(ex.getMessage());
                    JOptionPane.showMessageDialog(this, "Error populating tables. Rolled back changes.\n" + ex.getMessage(),"SQL Error", JOptionPane.ERROR_MESSAGE);
                }
            /* Handle connection-level errors */
            } catch (SQLException ex) {
                log("\nDatabase connection error: " + ex.getMessage());
                JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(),"Database Error", JOptionPane.ERROR_MESSAGE);
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
