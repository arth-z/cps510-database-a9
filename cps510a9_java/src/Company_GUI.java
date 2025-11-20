import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Company_GUI extends JPanel{

        public Company_GUI(DB_GUI gui, String username, String password) {
            setLayout(new BorderLayout());

            JLabel title = new JLabel("Company Dashboard", SwingConstants.CENTER);
            title.setFont(new Font("Times New Roman", Font.BOLD, 28));
            add(title, BorderLayout.NORTH);

            JTextArea info = new JTextArea();
            info.setEditable(false);
            info.setText("Welcome Company!\n\nHere you will:\n- Manage job postings\n- View applications\n- Communicate with recruiters");
            add(info, BorderLayout.CENTER);

            JButton exit = new JButton("Exit");
            exit.addActionListener(e -> System.exit(0));
            add(exit, BorderLayout.SOUTH);
    }
    
}
