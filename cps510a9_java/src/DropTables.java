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
            boolean allSuccessful = true;
            StringBuilder errors = new StringBuilder();

            // Get the current connection object from dbConnection
            Connection connection = dbConnection.getConnection();

            try {
                // Table drop statements
                dbConnection.executeUpdate("DROP TABLE Interview CASCADE CONSTRAINTS");
                dbConnection.executeUpdate("DROP TABLE Resume CASCADE CONSTRAINTS");
                dbConnection.executeUpdate("DROP TABLE JobApplication CASCADE CONSTRAINTS");
                dbConnection.executeUpdate("DROP TABLE JobApplicant CASCADE CONSTRAINTS");
                dbConnection.executeUpdate("DROP TABLE Job CASCADE CONSTRAINTS");
                dbConnection.executeUpdate("DROP TABLE Recruiter CASCADE CONSTRAINTS");
                dbConnection.executeUpdate("DROP TABLE Company CASCADE CONSTRAINTS");
            } catch (SQLException ex) {
                allSuccessful = false;
                errors.append(ex.getMessage()).append("\n");
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
