import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ViewTables extends JPanel {

    private final DBConnection dbConnection;

    public ViewTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password); // connect

        // set up the GUI
        setLayout(new BorderLayout());

        JLabel title = new JLabel("View Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton q1 = new JButton("View each job applicant with the jobs they've applied for");
        JButton q2 = new JButton("View all scheduled interviews with job and applicant details");
        JButton q3 = new JButton("View of job postings each company and recruiter has");
        JButton q4 = new JButton("View of jobs with additional analytics info applied");
        JButton q5 = new JButton("View of active applicants");
        JButton q6 = new JButton("View of recruiters with additional analytic info applied");
        JButton q7 = new JButton("View of applicants with interviews, listing company and date");
        JButton q8 = new JButton("View of job application details, showing company/job/title/status");
        JButton q9 = new JButton("View of recruiters, their companies, and interviews for jobs they manage");

        JButton[] buttons = {q1, q2, q3, q4, q5, q6, q7, q8, q9};
        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 18));
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());
        add(backButton, BorderLayout.SOUTH);

        // define the view creation queries
        String view1 =
            "CREATE or REPLACE VIEW ApplicantJobInfo AS " +
                "SELECT " +
                    "a.applicantID, " +
                    "a.first_name, " +
                    "a.last_name, " +
                    "j.title AS job_title, " +
                    "c.name AS company_name, " +
                    "r.first_name || ' ' || r.last_name AS recruiter_name " +
                "FROM JobApplicant a, JobApplication ja, Job j, Company c, Recruiter r " + 
                "WHERE a.applicantID = ja.applicantID " +
                    "AND ja.jobID = j.jobID " +
                    "AND j.companyID = c.companyID " +
                    "AND j.recruiterID = r.recruiterID";

        String view2 =
            "CREATE or REPLACE VIEW InterviewSchedules AS " +
                "SELECT " +
                    "i.interviewID, " +
                    "a.first_name || ' ' || a.last_name AS applicant_name, " +
                    "j.title AS job_title, " +
                    "r.first_name || ' ' || r.last_name AS recruiter_name, " +
                    "i.location " +
                "FROM Interview i, JobApplication ja, Job j, Recruiter r, JobApplicant a " +
                "WHERE i.jobAppID = ja.jobAppID " +
                    "AND ja.jobID = j.jobID " +
                    "AND ja.applicantID = a.applicantID " +
                    "AND j.recruiterID = r.recruiterID";  

        String view3 =
            "CREATE or REPLACE VIEW CompanyJobSummary AS " +
                "SELECT " +
                    "c.name AS company_name, " +
                    "r.first_name || ' ' || r.last_name AS recruiter_name, " +
                    "COUNT(j.jobID) AS total_jobs_posted " +
                "FROM Company c, Job j, Recruiter r " +
                "WHERE c.companyID = j.companyID " +
                    "AND j.recruiterID = r.recruiterID " +
                "GROUP BY c.name, r.first_name, r.last_name " +
                "ORDER BY c.name ASC";

        String view4 =
            "CREATE or REPLACE VIEW JobData(\"Job ID\", \"Title\", \"Employer\", \"Employer ID\", \"Posted by\", \"Salary\", \"Working Hours\", \"Date Posted\", \"Location\", \"Description\", \"Applications Received\") AS " +
                "SELECT Job.jobID, Job.title, Company.name, Company.companyID, Recruiter.first_name || ' ' || Recruiter.last_name, Job.salary, Job.workingHours, Job.datePosted, Job.location, Job.description, " +
                    "COUNT(JobApplication.jobAppID) AS \"Applications Received\" " +
                    "FROM Job LEFT JOIN JobApplication ON Job.jobID = JobApplication.jobID " +
                        "JOIN Company ON Job.companyID = Company.companyID " +
                        "JOIN Recruiter ON Job.recruiterID = Recruiter.recruiterID " +
                    "GROUP BY Job.jobID, Job.title, Company.name, Company.companyID, Recruiter.first_name || ' ' || Recruiter.last_name, Job.salary, Job.workingHours, Job.datePosted, Job.location, Job.description " +
                    "ORDER BY Job.jobID";

        String view5 =
            "CREATE or REPLACE VIEW ActiveApplicants (\"Applicant ID\", \"First Name\", \"Last Name\", \"Address\", \"Email\", \"Phone\", \"Application Count\", \"Resume Count\") AS " +
                "SELECT JobApplicant.applicantID, JobApplicant.first_name, JobApplicant.last_name, JobApplicant.address, JobApplicant.email, JobApplicant.phone, " +
                    "COUNT(DISTINCT JobApplication.jobAppID) AS \"Application Count\", " +
                    "COUNT(DISTINCT Resume.resumeID) AS \"Resume Count\" " +
                    "FROM JobApplication JOIN JobApplicant ON JobApplicant.applicantID = JobApplication.applicantID " +
                    "JOIN Resume ON Resume.applicantID = JobApplicant.applicantID " +
                    "GROUP BY JobApplicant.applicantID, JobApplicant.first_name, JobApplicant.last_name, JobApplicant.address, JobApplicant.email, JobApplicant.phone " +
                    "HAVING COUNT(DISTINCT JobApplication.jobAppID) >= 1 AND COUNT(DISTINCT Resume.resumeID) >= 1 " +
                    "ORDER BY JobApplicant.applicantID";
        
        String view6 =
            "CREATE or REPLACE VIEW RecruiterData (\"Recruiter ID\", \"First Name\", \"Last Name\", \"Company ID\", \"Company Name\", \"Email\", \"Phone\", \"Jobs Posted\", \"Applications Received\", \"Interviews Scheduled\") AS " +
            "SELECT Recruiter.recruiterID, Recruiter.first_name, Recruiter.last_name, Company.companyID, Company.name, Recruiter.email, Recruiter.phone, " +
                "COUNT(DISTINCT Job.jobID), " +
                "COUNT(DISTINCT JobApplication.jobAppID), " +
                "COUNT(DISTINCT Interview.interviewID) " +
                "FROM Recruiter LEFT JOIN Job ON Recruiter.recruiterID = Job.recruiterID " +
                    "LEFT JOIN JobApplication on JobApplication.jobID = Job.jobID " +
                    "LEFT JOIN Interview on Interview.jobAppID = JobApplication.jobAppID " +
                    "JOIN Company on Recruiter.companyID = Company.companyID " +
                "GROUP BY Recruiter.recruiterID, Recruiter.first_name, Recruiter.last_name, Company.companyID, Company.name, Recruiter.email, Recruiter.phone " +
                "ORDER BY Recruiter.recruiterID";


        String view7 =
            "CREATE or REPLACE VIEW ApplicantInterview AS " +
                "SELECT JobApplicant.applicantID, first_name, last_name, Company.name AS companyName, Interview.dateTime " +
                "FROM JobApplicant, JobApplication, Interview, Company, Job " +
                "WHERE JobApplicant.applicantID = JobApplication.applicantID AND JobApplication.jobAppID = Interview.jobAppID AND JobApplication.jobID = Job.jobID AND Job.companyID = Company.companyID";

        String view8 =
            "CREATE or REPLACE VIEW JobApplicationDetails AS " +
                "SELECT JobApplication.jobAppID, JobApplicant.last_name, JobApplicant.first_name, Job.title, Company.name as companyName, JobApplication.status " +
                "FROM JobApplication, JobApplicant, Job, Company " +
                "WHERE JobApplication.applicantID = JobApplicant.applicantID AND JobApplication.jobID = Job.jobID AND Job.companyID = Company.companyID";
        
        String view9 = 
            "CREATE or REPLACE VIEW RecruiterInterview AS " +
                "SELECT Recruiter.recruiterID, Recruiter.last_name, Recruiter.first_name, Company.name AS companyName, Job.title, Interview.dateTime " +
                "FROM Recruiter, Company, Interview, JobApplication, Job " +
                "WHERE Recruiter.companyID = Company.companyID AND Job.recruiterID = Recruiter.recruiterID AND Job.jobID = JobApplication.jobID AND JobApplication.jobAppID = Interview.jobAppID";

        // attach a query functionality to each button, as well as specify a query to show the created view
        attachViewToButton(q1, view1, "SELECT * FROM ApplicantJobInfo");
        attachViewToButton(q2, view2, "SELECT * FROM InterviewSchedules");
        attachViewToButton(q3, view3, "SELECT * FROM CompanyJobSummary");
        attachViewToButton(q4, view4, "SELECT * FROM JobData");
        attachViewToButton(q5, view5, "SELECT * FROM ActiveApplicants");
        attachViewToButton(q6, view6, "SELECT * FROM RecruiterData");
        attachViewToButton(q7, view7, "SELECT * FROM ApplicantInterview");
        attachViewToButton(q8, view8, "SELECT * FROM JobApplicationDetails");
        attachViewToButton(q9, view9, "SELECT * FROM RecruiterInterview");
    }

    // attach functionality to a button
    // essentially - execute the view queries, then show the view results in a table (using the showViewQuery parameter)
    private void attachViewToButton(JButton button, String viewQuery, String showViewQuery) {
        button.addActionListener(e -> {
            try {
                dbConnection.executeUpdate(viewQuery); // execute the view query
                JOptionPane.showMessageDialog(null, "View created successfully! Now showing view...", "Success", JOptionPane.INFORMATION_MESSAGE); // create the view

                try {
                    ResultSet rs = dbConnection.executeQuery(showViewQuery); // now connect again and try and execute the show view query
                    JTable table = new JTable(DB_GUI.buildTableModel(rs));
                    JOptionPane.showMessageDialog(null, new JScrollPane(table), "Query Results", JOptionPane.INFORMATION_MESSAGE);
                    JOptionPane.showMessageDialog(null, "View displayed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) { // catch any SQL errors (something going wrong showing the view)
                    JOptionPane.showMessageDialog(null, "Error showing view:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) { // catch any SQL errors (something going wrong creating the view)
                JOptionPane.showMessageDialog(null, "Error creating view:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}