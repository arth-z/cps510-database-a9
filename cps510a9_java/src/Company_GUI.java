import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;
import java.util.Vector;

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

        /* Add button functionality */
        manageJobs.addActionListener(e -> viewCompanyJobs());
        manageRecruiters.addActionListener(e -> viewRecruiters());
        viewApplicants.addActionListener(e -> viewApplicantsForCompany());
        scheduleInterview.addActionListener(e -> viewInterviews());
        viewApps.addActionListener(e -> viewApplications());
        exit.addActionListener(e -> System.exit(0));

    }

    /* Helper: Makes table non-editable */
    private DefaultTableModel readOnlyModel(ResultSet rs) throws SQLException {
        DefaultTableModel model = DB_GUI.buildTableModel(rs);

        Vector<String> colNames = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++)
            colNames.add(model.getColumnName(i));

        return new DefaultTableModel(model.getDataVector(), colNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    /* View Company Jobs feature */
    private void viewCompanyJobs() {
        try {
            String sql =
                "SELECT j.title AS \"Job Title\", j.location AS \"Location\", " +
                "j.salary AS \"Salary ($/hr)\", j.workingHours AS \"Hours/Week\", " +
                "TO_CHAR(j.datePosted, 'YYYY-MM-DD') AS \"Posted On\", " +
                "j.description AS \"Description\" " +
                "FROM Job j WHERE j.companyID = " + companyID;

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            JOptionPane.showMessageDialog(this, new JScrollPane(table), "Company Job Listings", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* View Recruiters for the Company */
    private void viewRecruiters() {
        try {
            String sql =
                "SELECT first_name AS \"First Name\", last_name AS \"Last Name\", " +
                "email AS \"Email\", phone AS \"Phone\" " +
                "FROM Recruiter WHERE companyID = " + companyID;

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            JOptionPane.showMessageDialog(this, new JScrollPane(table), "Company Recruiters", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* View Job Applicants for Jobs Posted by the Company */
    private void viewApplicantsForCompany() {
        try {
            String sql =
                "SELECT ja.first_name || ' ' || ja.last_name AS \"Applicant\", " +
                "j.title AS \"Applied Job\", " +
                "a.status AS \"Application Status\", " +
                "TO_CHAR(a.dateTime, 'YYYY-MM-DD') AS \"Applied On\" " +
                "FROM JobApplication a " +
                "JOIN Job j ON a.jobID = j.jobID " +
                "JOIN JobApplicant ja ON a.applicantID = ja.applicantID " +
                "WHERE j.companyID = " + companyID;

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            JOptionPane.showMessageDialog(this, new JScrollPane(table), "Applicants for Company Jobs", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* View Interviews for the Applicants that Applied for the Jobs Posted by the Company */
    private void viewInterviews() {
        try {
            String sql =
                "SELECT ja.first_name || ' ' || ja.last_name AS \"Applicant\", " +
                "j.title AS \"Job\", " +
                "TO_CHAR(i.dateTime, 'YYYY-MM-DD HH:MI AM') AS \"Interview Time\", " +
                "i.location AS \"Location\" " +
                "FROM Interview i " +
                "JOIN JobApplication a ON i.jobAppID = a.jobAppID " +
                "JOIN Job j ON a.jobID = j.jobID " +
                "JOIN JobApplicant ja ON a.applicantID = ja.applicantID " +
                "WHERE j.companyID = " + companyID;

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            JOptionPane.showMessageDialog(this, new JScrollPane(table), "Interviews Scheduled", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* View Job Applications for the Jobs Posted by the Company */
    private void viewApplications() {
        try {
            String sql =
                "SELECT ja.first_name || ' ' || ja.last_name AS \"Applicant\", " +
                "j.title AS \"Job Title\", " +
                "a.status AS \"Status\", " +
                "TO_CHAR(a.dateTime, 'YYYY-MM-DD') AS \"Applied On\" " +
                "FROM JobApplication a " +
                "JOIN Job j ON a.jobID = j.jobID " +
                "JOIN JobApplicant ja ON a.applicantID = ja.applicantID " +
                "WHERE j.companyID = " + companyID;

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            JOptionPane.showMessageDialog(this, new JScrollPane(table), "All Applications", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
