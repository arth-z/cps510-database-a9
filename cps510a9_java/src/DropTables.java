import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DropTables extends JPanel{

    private final DBConnection dbConnection; 
    
    public DropTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Drop Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JButton executeButton = new JButton("Drop Tables (Default)");
        executeButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        executeButton.addActionListener(e -> {
            String[] tables = {"Interview", "Resume", "JobApplication", "JobApplicant", "Job", "Recruiter", "Company"};
            boolean allSuccessful = true;
            StringBuilder errors = new StringBuilder();
            
            for (String table : tables) {
                try {
                    dbConnection.executeUpdate("DROP TABLE " + table + " CASCADE CONSTRAINTS");
                } catch (SQLException ex) {
                    /* Ignore "table does not exist", but log other issues */
                    if (!ex.getMessage().contains("ORA-00942")) {
                        allSuccessful = false;
                        errors.append("Failed to drop table ").append(table)
                            .append(": ").append(ex.getMessage()).append("\n");
                    }
                }
            }

            if (allSuccessful) {
                try {
                    dbConnection.commit();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "All tables dropped successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                try {
                    dbConnection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                JOptionPane.showMessageDialog(this, "Errors occurred while dropping tables:\n" + errors.toString(), "Errors", JOptionPane.ERROR_MESSAGE);
            }
        });
        add(executeButton, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());
        add(backButton, BorderLayout.SOUTH);

    }
}
