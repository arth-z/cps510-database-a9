
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Recruiter_GUI extends JPanel{

        public Recruiter_GUI(DB_GUI gui, String username, String password) {
            setLayout(new BorderLayout());

            JLabel title = new JLabel("Recruiter Dashboard", SwingConstants.CENTER);
            title.setFont(new Font("Times New Roman", Font.BOLD, 28));
            add(title, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            Color LightBlue = new Color(173, 216, 230);

            JButton viewRecruiters = new JButton("View Recruiters");
            JButton addRecruiter = new JButton("Add Recruiter");
            JButton viewCompanyJobs = new JButton("View Company Jobs");
            JButton evaluate = new JButton("Evaluate Applications");
            JButton interviews = new JButton("Upcoming Interviews");
            JButton exit = new JButton("Exit");    

            JButton[] buttons = {viewRecruiters, addRecruiter, viewCompanyJobs, evaluate, interviews, exit};
            
            for (JButton b : buttons) {
                b.setFont(new Font("Times New Roman", Font.BOLD, 20));
                b.setBackground(LightBlue);
                buttonPanel.add(b);
            }

            add(buttonPanel, BorderLayout.CENTER);
            
            exit.addActionListener(e -> System.exit(0));
    }    
    
}
