import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Company_GUI extends JPanel{

    private final DBConnection dbConnection; 
    private final String username;
    private final String password;
    private final int companyID;

        public Company_GUI(DB_GUI gui, String username, String password) throws SQLException {

            this.dbConnection = DBConnection.getInstance(username, password); 
            this.username = username;
            this.password = password; 
            this.companyID = gui.getCompanyID();

            setLayout(new BorderLayout());

            JLabel title = new JLabel("Company Dashboard", SwingConstants.CENTER);
            title.setFont(new Font("Times New Roman", Font.BOLD, 28));
            add(title, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
            Color LightBlue = new Color(173, 216, 230);

            JButton manageJobs = new JButton("View Company Job Listings");
            JButton manageRecruiters = new JButton("View Recruiters"); 
            JButton viewApplicants = new JButton("View Applicants");
            JButton scheduleInterview = new JButton("View Interviews");
            JButton viewApps = new JButton("View Applications");
            JButton exit = new JButton("Exit");

            JButton[] buttons = {manageJobs, manageRecruiters, viewApplicants, scheduleInterview, viewApps, exit};

            for (JButton b : buttons) {
                b.setFont(new Font("Times New Roman", Font.BOLD, 20));
                b.setBackground(LightBlue);
                buttonPanel.add(b);
            }

            add(buttonPanel, BorderLayout.CENTER);

            exit.addActionListener(e -> System.exit(0));
    }
    
}
