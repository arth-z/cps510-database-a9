import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Company_GUI extends JPanel{

        public Company_GUI(DB_GUI gui, String username, String password) {
            setLayout(new BorderLayout());

            JLabel title = new JLabel("Company Dashboard", SwingConstants.CENTER);
            title.setFont(new Font("Times New Roman", Font.BOLD, 28));
            add(title, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            Color LightBlue = new Color(173, 216, 230);

            JButton postJob = new JButton("Post a Job");
            JButton manageJobs = new JButton("Manage Job Listings");
            JButton viewApplicants = new JButton("View Applicants");
            JButton scheduleInterview = new JButton("Schedule Interview");
            JButton evaluate = new JButton("Evaluate Applications");
            JButton exit = new JButton("Exit");

            JButton[] buttons = {postJob, manageJobs, viewApplicants, scheduleInterview, evaluate, exit};

            for (JButton b : buttons) {
                b.setFont(new Font("Times New Roman", Font.BOLD, 20));
                b.setBackground(LightBlue);
                buttonPanel.add(b);
            }

            add(buttonPanel, BorderLayout.CENTER);

            exit.addActionListener(e -> System.exit(0));
    }
    
}
