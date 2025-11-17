import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/* This class allows user to drop (delete) all database tables for the application */
public class DropTables extends JPanel{

    private final DBConnection dbConnection;  /* Handles connection to Oracle database */
    private final JTextArea outputArea = new JTextArea(); /* Displays SQL output and logs */

    public DropTables(DB_GUI gui, String username, String password) throws SQLException {
        /* Establish a database connection */
        this.dbConnection = DBConnection.getInstance(username, password);

        /* Set the panel layout */
        setLayout(new BorderLayout());

        /* Add a title label at the top */
        JLabel title = new JLabel("Drop Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        /* Configure the SQL output area for viewing results */
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("SQL Output"));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);

        /* Create a panel for user buttons at the bottom */
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        /* Button to execute table deletion */
        JButton executeButton = new JButton("Drop Tables");
        executeButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        executeButton.addActionListener(e -> dropTables());

        /* Button to view the SQL output in a separate window */
        JButton viewOutputButton = new JButton("View Output");
        viewOutputButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        viewOutputButton.addActionListener(e -> gui.addOutputPanel("DropTablesOutput", outputArea));

        /* Button to return to the main menu */
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());

        /* Add buttons to the button panel */
        buttonPanel.add(executeButton);
        buttonPanel.add(viewOutputButton);
        buttonPanel.add(backButton);

        /* Add the button panel to the main layout */
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /* Executes DROP TABLE commands for all database tables in the correct order. Ensures that dependent tables (e.g., Interview, Resume) are dropped before parent tables */
    private void dropTables() {
        /* Clear previous output before new operation */
        outputArea.setText("");
        log("Starting table drop process...\n");

        /* Define tables in reverse order of creation (drop child tables first) */
        String[] tables = {"Interview", "Resume", "JobApplication", "JobApplicant", "Job", "Recruiter", "Company"};
        /* Track whether all tables dropped successfully */
        boolean allSuccessful = true;
            
        try {
            /* Get database connection and start manual transaction control */
            Connection connection = dbConnection.getConnection();
            connection.setAutoCommit(false); 

            /* Loop through and drop each table */
            for (String table : tables) {
                try {
                    /* Attempt to drop the table, including all foreign key constraints */
                    dbConnection.executeUpdate("DROP TABLE " + table + " CASCADE CONSTRAINTS");
                    log("Table " + table.toUpperCase() + " dropped successfully.");
                } catch (SQLException ex) {
                    /* Ignore "table does not exist" errors but log others */
                    if (ex.getMessage().contains("ORA-00942")) {
                        log("Table " + table.toUpperCase() + " does not exist (skipped).");
                    } else {
                        allSuccessful = false;
                        log("Failed to drop " + table.toUpperCase() + ": " + ex.getMessage());
                    }
                }
            }

            /* Commit or rollback depending on success */
            if (allSuccessful) {
                    connection.commit();
                    log("\nAll tables dropped successfully!");
                    JOptionPane.showMessageDialog(this, "All tables dropped successfully!");
            } else {
                connection.rollback();
                log("\nErrors occurred while dropping tables. Rolled back changes.");
                JOptionPane.showMessageDialog(this, "Some tables could not be dropped. Rolled back changes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        /* Handle database connection issues or other critical SQL errors */
        } catch (SQLException ex) {
            log("\nDatabase connection error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database connection error: " + ex.getMessage(),"Database Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Helper to append text to output area */
    private void log(String message) {
        outputArea.append(message + "\n");
    }
}
