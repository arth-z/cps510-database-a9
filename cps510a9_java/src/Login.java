import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

        /* Create text fields for user input */
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

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

        /* Add login button below the input fields */
        gbc.gridx = 1; gbc.gridy = 2;
        add(loginButton, gbc);

        /* Add event listener for login button. When the button is clicked, it retrieves the entered credentials and attempts to connect to Oracle DB. */
        loginButton.addActionListener(e -> {
            /* Retrieve user input values */
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            try {
                /* Attempt to establish database connection using DBConnection instance */
                DBConnection.getInstance(user, pass);
                /* If successful, display sucess popup */
                JOptionPane.showMessageDialog(this, "Credentials Entry Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                /* Store credentials in main GUI and navigate to main menu */
                gui.setCredentials(user, pass);
                gui.showMainMenu();
            
            /* If login fails, display error message */
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Credentials Entry failed. Check your credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

