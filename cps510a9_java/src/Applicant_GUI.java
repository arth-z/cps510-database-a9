
import java.awt.*;
import javax.swing.*;
import java.sql.*; 

public class Applicant_GUI extends  JPanel{

    public Applicant_GUI(DB_GUI gui, String username, String password) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Applicant Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setText("Welcome Applicant!\n\nHere you will:\n- Browse jobs\n- Apply to jobs\n- View your applications");
        add(info, BorderLayout.CENTER);

        JButton exit = new JButton("Exit");
        exit.addActionListener(e -> System.exit(0));
        add(exit, BorderLayout.SOUTH);
    }
    
}
