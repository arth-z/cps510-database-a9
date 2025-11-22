import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/* This class represents the login screen for the application. */
public class Login extends JPanel {

    public Login(DB_GUI gui) {
        /* Use GridBagLayout for flexible UI element placement */
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        /* Adds padding around each component */
        gbc.insets = new Insets(10, 10, 10, 10);

        /* Create labels for username and password input fields */
        JLabel userLabel = new JLabel("Oracle Username:");
        JLabel passLabel = new JLabel("Oracle Password:");
        JLabel roleLabel = new JLabel("Role:");

        /* Create text fields for user input */
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

        /* Create a dropdown (combo box) for selecting the user role */
        JComboBox<String> roleSelector = new JComboBox<>(new String[]{
            "Applicant",
            "Recruiter",
            "Company",
            "Database Admin"
        });

        /* Create login button */
        JButton loginButton = new JButton("Login");

        /* Add username label and input field to panel */
        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(userField, gbc);

        /* Add password label and input field to panel */
        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        /* Add role label and dropdown selector */
        gbc.gridx = 0; gbc.gridy = 2;
        add(roleLabel, gbc);
        gbc.gridx = 1;
        add(roleSelector, gbc);

        /* Add login button below the input fields */
        gbc.gridx = 1; gbc.gridy = 3;
        add(loginButton, gbc);

        /* Add event listener for login button. When the button is clicked, it retrieves the entered credentials and attempts to connect to Oracle DB. */
        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());
            String selectedRole = (String) roleSelector.getSelectedItem();

            try {
                DBConnection.getInstance(user, pass);

                gui.setCredentials(user, pass);
                gui.setUserRole(selectedRole);

                /* Run DatabaseInitializer for non-admin roles */
                if (!selectedRole.equals("Database Admin")) {
                    DatabaseInitializer init = new DatabaseInitializer(user, pass);
                    init.initialize();
                }

                /* If applicant logs in, map their email to applicantID */
                if (selectedRole.equals("Applicant")) {
                    String email = JOptionPane.showInputDialog(this, "Enter your applicant email:", "Applicant Login", JOptionPane.QUESTION_MESSAGE);

                    if (email == null || email.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Email required.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    /* Query the database */
                    Connection connection = DBConnection.getInstance(user, pass).getConnection();
                    PreparedStatement stmt = connection.prepareStatement(
                        "SELECT applicantID FROM JobApplicant WHERE email = ?"
                    );
                    stmt.setString(1, email.trim());
                    ResultSet rs = stmt.executeQuery();

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "No applicant found with this email.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int applicantID = rs.getInt(1);
                    gui.setApplicantID(applicantID);
                    System.out.println("Logged in as applicant: ID = " + applicantID);

                } else if (selectedRole.equals("Recruiter")) { 
                    String email = JOptionPane.showInputDialog(this, "Enter your recruiter email:", "Recruiter Login", JOptionPane.QUESTION_MESSAGE);

                    if (email == null || email.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Email required.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    /* Query the database */
                    Connection connection = DBConnection.getInstance(user, pass).getConnection();
                    PreparedStatement stmt = connection.prepareStatement(
                        "SELECT recruiterID, companyID FROM Recruiter WHERE email = ?"
                    );
                    stmt.setString(1, email.trim());
                    ResultSet rs = stmt.executeQuery();

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "No recruiter found with this email.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int recruiterID = rs.getInt(1);
                    int companyID = rs.getInt(2);
                    gui.setRecruiterID(recruiterID);
                    gui.setCompanyID(companyID); 
                    System.out.println("Logged in as recruiter: ID = " + recruiterID);
                    
                }  else if (selectedRole.equals("Company")) { 
                    String name = JOptionPane.showInputDialog(this, "Enter your company name:", "Company Login", JOptionPane.QUESTION_MESSAGE);

                    if (name == null || name.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Company Name required.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    /* Query the database */
                    Connection connection = DBConnection.getInstance(user, pass).getConnection();
                    PreparedStatement stmt = connection.prepareStatement(
                        "SELECT companyID FROM Company WHERE name = ?"
                    );
                    stmt.setString(1, name.trim());
                    ResultSet rs = stmt.executeQuery();

                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(this, "No company found with this name.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int companyID = rs.getInt(1);
                    gui.setCompanyID(companyID);
                    System.out.println("Logged in as Company: ID = " + companyID);
                } 

                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                switch (selectedRole) {
                    case "Applicant":
                        gui.showApplicantGUI();
                        break;
                    case "Recruiter":
                        gui.showRecruiterGUI();
                        break;
                    case "Company":
                        gui.showCompanyGUI();
                        break;
                    default:
                        gui.showMainMenu();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Login failed. Check your credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    }
}

