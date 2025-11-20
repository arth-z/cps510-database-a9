
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Recruiter_GUI extends JPanel{

        public Recruiter_GUI(DB_GUI gui, String username, String password) {
            setLayout(new BorderLayout());

            JLabel title = new JLabel("Recruiter Dashboard", SwingConstants.CENTER);
            title.setFont(new Font("Times New Roman", Font.BOLD, 28));
            add(title, BorderLayout.NORTH);

            JTextArea info = new JTextArea();
            info.setEditable(false);
            info.setText("Welcome Recruiter!\n\nHere you will:\n- Post jobs\n- Review applicants\n- Schedule interviews");
            add(info, BorderLayout.CENTER);

            JButton exit = new JButton("Exit");
            exit.addActionListener(e -> System.exit(0));
            add(exit, BorderLayout.SOUTH);
    }    
    
}
