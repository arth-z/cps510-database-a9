
import java.awt.*;
import javax.swing.*;
import java.sql.*; 

public class Applicant_GUI extends  JPanel{

    public Applicant_GUI(DB_GUI gui, String username, String password) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Applicant Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        Color LightBlue = new Color(173, 216, 230);

        JButton browseJobs = new JButton("Browse Jobs");
        JButton applyJob = new JButton("Apply for a Job");
        JButton viewApps = new JButton("View My Applications");
        JButton viewInterviews = new JButton("View Interviews");
        JButton updateProfile = new JButton("Update Profile");
        JButton exit = new JButton("Exit");

        JButton[] buttons = {
            browseJobs, applyJob, viewApps, viewInterviews, updateProfile, exit
        };

        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 20));
            b.setBackground(LightBlue);
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

        exit.addActionListener(e -> System.exit(0));
    }
    
}
