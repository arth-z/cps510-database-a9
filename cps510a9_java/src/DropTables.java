import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/* This class allows user to drop (delete) all database tables for the application */
public class DropTables extends JPanel{

    private final DBConnection dbConnection;  /* Handles connection to Oracle database */
    private final JTextArea outputArea = new JTextArea(); /* Displays SQL output and logs */
    
    public DropTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Drop Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.setBorder(BorderFactory.createTitledBorder("SQL Output"));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton executeButton = new JButton("Drop Tables");
        executeButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        executeButton.addActionListener(e -> dropTables());

        JButton viewOutputButton = new JButton("View Output");
        viewOutputButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        viewOutputButton.addActionListener(e -> gui.addOutputPanel("DropTablesOutput", outputArea));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());

        buttonPanel.add(executeButton);
        buttonPanel.add(viewOutputButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void dropTables() {
        outputArea.setText("");
        log("Starting table drop process...\n");

        String[] tables = {"Interview", "Resume", "JobApplication", "JobApplicant", "Job", "Recruiter", "Company"};
        boolean allSuccessful = true;
            
        try {
            Connection connection = dbConnection.getConnection();
            connection.setAutoCommit(false); 

            for (String table : tables) {
                try {
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

            if (allSuccessful) {
                    connection.commit();
                    log("\nAll tables dropped successfully!");
                    JOptionPane.showMessageDialog(this, "All tables dropped successfully!");
            } else {
                connection.rollback();
                log("\nErrors occurred while dropping tables. Rolled back changes.");
                JOptionPane.showMessageDialog(this, "Some tables could not be dropped. Rolled back changes.", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
