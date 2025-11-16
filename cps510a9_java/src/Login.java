import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JPanel {

    public Login(DB_GUI gui) {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Oracle Username:");
        JLabel passLabel = new JLabel("Oracle Password:");

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);

        JButton loginButton = new JButton("Login");

        gbc.gridx = 0; gbc.gridy = 0;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passField, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String user = userField.getText();
            String pass = new String(passField.getPassword());

            try {
                DBConnection.getInstance(user, pass);

                JOptionPane.showMessageDialog(this, "Credentials Entry Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                gui.setCredentials(user, pass);
                gui.showMainMenu();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Credentials Entry failed. Check your credentials.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

