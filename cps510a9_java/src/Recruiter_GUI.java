import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class Recruiter_GUI extends JPanel{

    //Database connection and for user credentials
    private final DBConnection dbConnection; 
    private final String username;
    private final String password;
    private final int recruiterID;
    private final int companyID;

    //Constructor that sets up the recruiter dashboard 
    public Recruiter_GUI(DB_GUI gui, String username, String password) throws SQLException {
        
        this.dbConnection = DBConnection.getInstance(username, password); 
        this.username = username;
        this.password = password; 
        this.recruiterID = gui.getRecruiterID();
        this.companyID = gui.getCompanyID();

        //Sets the main layout
        setLayout(new BorderLayout());

        //Creating and styling the label for title
        JLabel title = new JLabel("Recruiter Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);


        //Panel creation for the buttons in a grid layout
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        Color LightBlue = new Color(173, 216, 230);

        //Create buttons for each recruiter function
        JButton createJobs = new JButton("Create Job Posting"); 
        JButton manageCompanyJobs = new JButton("Manage Company Jobs");
        JButton evaluate = new JButton("Evaluate Applications");
        JButton scheduleInterview = new JButton("Schedule Interview");
        JButton interviews = new JButton("Upcoming Interviews");
        JButton viewApplicants = new JButton("View Applicants");
        JButton exit = new JButton("Exit");    

        JButton[] buttons = {createJobs, manageCompanyJobs, evaluate, scheduleInterview, interviews, viewApplicants, exit};
        
        //Style the buttons with a nice font and color
        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 20));
            b.setBackground(LightBlue);
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

        //Connect the buttons to each of its corresponding function
        createJobs.addActionListener(e -> createJobPosting());
        manageCompanyJobs.addActionListener(e -> manageCompanyJobs());
        evaluate.addActionListener(e -> evaluateApplications());
        scheduleInterview.addActionListener(e -> scheduleInterview());
        interviews.addActionListener(e -> viewUpcomingInterviews());
        viewApplicants.addActionListener(e -> viewApplicants());
        exit.addActionListener(e -> System.exit(0));
    }

    //Make a resultset non editable table so that its only for display
    private DefaultTableModel readOnlyModel(ResultSet rs) throws SQLException {
        DefaultTableModel model = DB_GUI.buildTableModel(rs);
        Vector<String> colNames = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++)
            colNames.add(model.getColumnName(i));

        return new DefaultTableModel(model.getDataVector(), colNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    //Extracts data from a selected table row and organizes it into a key-value format
    private Map<String, Object> extractRow(JTable table, int row) {
        Map<String, Object> map = new LinkedHashMap<>();
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (int col = 0; col < model.getColumnCount(); col++) {
            String key = model.getColumnName(col);
            Object val = model.getValueAt(row, col);
            map.put(key, val);
        }
        return map;
    }

    //Displays information in a pop up window
    private void showDetailsPopup(String title, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();

        for (String key : data.keySet()) {
            sb.append(key).append(": ").append(data.get(key)).append("\n");
        }

        //Creates a scrollable area of text which displays the results
        JTextArea text = new JTextArea(sb.toString());
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(text);
        scroll.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scroll, title, JOptionPane.PLAIN_MESSAGE);
    }

    //Recruiters can create new job postings for their company
    private void createJobPosting() {
        try {
            JTextField jobID = new JTextField();
            JTextField salary = new JTextField();
            JTextField workingHours = new JTextField();
            JTextField datePosted = new JTextField();
            JTextField location = new JTextField();
            JTextField title = new JTextField();
            JTextField description = new JTextField();

            Object[] queryValues = {
                "JobID (mandatory int, unique):", jobID,
                "Salary:", salary,
                "Working Hours:", workingHours,
                "Date Posted (YYYY-MM-DD):", datePosted,
                "Location:", location,
                "Title (mandatory):", title,
                "Description (mandatory):", description
            };

            int option = JOptionPane.showConfirmDialog(null, queryValues, "Create Job Posting", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                //Insert the new job into the database
                String insertSQL = "INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                pstmt.setInt(1, Integer.parseInt(jobID.getText()));
              pstmt.setInt(2, companyID); 
                pstmt.setInt(3, recruiterID);
                pstmt.setFloat(4, salary.getText() == null || salary.getText().isEmpty() ? 0 : Float.parseFloat(salary.getText()));
                pstmt.setFloat(5, workingHours.getText() == null || workingHours.getText().isEmpty() ? 0 : Float.parseFloat(workingHours.getText()));
                pstmt.setDate(6, Date.valueOf(datePosted.getText()));
                pstmt.setString(7, location.getText());
                pstmt.setString(8, title.getText());
                pstmt.setString(9, description.getText());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Job posting created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create job posting.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error creating job posting: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Shows all job postings created by the recruiter with the number of applications displayed
    private void manageCompanyJobs() {
        try {
            String sql =
                "SELECT j.jobID, j.title AS \"Job Title\", j.location AS \"Location\", " +
                "j.salary AS \"Salary ($/hr)\", j.workingHours AS \"Hours/Week\", " +
                "TO_CHAR(j.datePosted, 'YYYY-MM-DD') AS \"Posted On\", " +
                "COUNT(ja.jobAppID) AS \"Applications\" " +
                "FROM Job j " +
                "LEFT JOIN JobApplication ja ON j.jobID = ja.jobID " +
                "WHERE j.recruiterID = " + recruiterID +
                " GROUP BY j.jobID, j.title, j.location, j.salary, j.workingHours, j.datePosted";

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            //A button to view the job information in more detail
            JButton viewBtn = new JButton("View Selected Job");
            viewBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a job first.");
                    return;
                }
                showDetailsPopup("Job Details", extractRow(table, row));
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(viewBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "My Job Postings", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Shows all applications for jobs posted by the recruiter with the ability to update the status
    private void evaluateApplications() {
        try {
            String sql =
                "SELECT ja.jobAppID, j.title AS \"Job Title\", " +
                "a.first_name || ' ' || a.last_name AS \"Applicant\", " +
                "ja.status AS \"Status\", " +
                "TO_CHAR(ja.dateTime, 'YYYY-MM-DD') AS \"Applied On\" " +
                "FROM JobApplication ja " +
                "JOIN Job j ON ja.jobID = j.jobID " +
                "JOIN JobApplicant a ON ja.applicantID = a.applicantID " +
                "WHERE j.recruiterID = " + recruiterID +
                " ORDER BY ja.dateTime DESC";

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            JButton updateBtn = new JButton("Update Application Status");
            updateBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select an application first.");
                    return;
                }
                //Get the selected application details
                String jobAppID = table.getValueAt(row, 0).toString();
                String currentStatus = table.getValueAt(row, 3).toString();
                String applicantName = table.getValueAt(row, 2).toString();
                
                String[] statusOptions = {"Submitted", "Under Review", "Interview Pending", "Rejected"};
                String newStatus = (String) JOptionPane.showInputDialog(this,
                    "Update status for " + applicantName + ":",
                    "Update Application Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statusOptions,
                    currentStatus);
                
                if (newStatus != null) {
                    updateApplicationStatus(jobAppID, newStatus);
                }
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(updateBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "Applications to Evaluate", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Helper function to update the application status in evaluate jobs 
    private void updateApplicationStatus(String jobAppID, String newStatus) {
        try {
            String sql = "UPDATE JobApplication SET status = ? WHERE jobAppID = ?";
            PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql);
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, Integer.parseInt(jobAppID));
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Application status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update application status.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Function that shows all applications that are not rejected to be selected for an interview
    private void scheduleInterview() {
        try {
            String sql = 
                "SELECT ja.jobAppID, j.title AS \"Job Title\", " +
                "a.first_name || ' ' || a.last_name AS \"Applicant\", " +
                "ja.status AS \"Application Status\" " +
                "FROM JobApplication ja " +
                "JOIN Job j ON ja.jobID = j.jobID " +
                "JOIN JobApplicant a ON ja.applicantID = a.applicantID " +
                "WHERE j.recruiterID = " + recruiterID +
                " AND ja.status IN ('Submitted', 'Under Review', 'Interview Pending')" +
                " ORDER BY ja.dateTime DESC";

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));
            
            JButton scheduleBtn = new JButton("Schedule Interview for Selected Application");
            scheduleBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select an application first.");
                    return;
                }

                //Extract application details from a selected row
                String jobAppID = table.getValueAt(row, 0).toString();
                String jobTitle = table.getValueAt(row, 1).toString();
                String applicantName = table.getValueAt(row, 2).toString();

                createInterview(jobAppID, jobTitle, applicantName);
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            
            JLabel infoLabel = new JLabel("Select an application to schedule an interview", SwingConstants.CENTER);
            infoLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            panel.add(infoLabel, BorderLayout.NORTH);
            panel.add(scheduleBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "Schedule Interview - Select Application", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading applications: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Function that creates the new interview record once selected
    private void createInterview(String jobAppID, String jobTitle, String applicantName) {
        try {
            JTextField interviewID = new JTextField();
            JTextField dateTime = new JTextField();
            JTextField location = new JTextField();

            Object[] queryValues = {
                "Application: " + jobTitle + " - " + applicantName,
                "InterviewID:", interviewID,
                "Date (YYYY-MM-DD):", dateTime,
                "Location:", location
            };

            int option = JOptionPane.showConfirmDialog(null, queryValues, "Schedule Interview", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                
                //Insert the interview record into the database
                String insertSQL = "INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (?, ?, TO_DATE(?, 'YYYY-MM-DD HH:MI AM'), ?)";
                PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                pstmt.setInt(1, Integer.parseInt(interviewID.getText()));
                pstmt.setInt(2, Integer.parseInt(jobAppID));
                pstmt.setString(3, dateTime.getText());
                pstmt.setString(4, location.getText());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    String updateSQL = "UPDATE JobApplication SET status = 'Interview Pending' WHERE jobAppID = ?";
                    PreparedStatement updateStmt = dbConnection.getConnection().prepareStatement(updateSQL);
                    updateStmt.setInt(1, Integer.parseInt(jobAppID));
                    updateStmt.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Interview scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    //Show upcoming interviews with the new added interview
                    viewUpcomingInterviews();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to schedule interview.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error scheduling interview: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


//Displays upcoming interviews for the recruiter's job applications
    private void viewUpcomingInterviews() {
         try {
            String sql =
                "SELECT ja.first_name || ' ' || ja.last_name AS \"Applicant\", " +
            "j.title AS \"Job\", " +
            "TO_CHAR(i.dateTime, 'YYYY-MM-DD HH:MI AM') AS \"Interview Time\", " +
            "i.location AS \"Location\", " +
            "a.status AS \"Application Status\" " +  
            "FROM Interview i " +
            "JOIN JobApplication a ON i.jobAppID = a.jobAppID " +
            "JOIN Job j ON a.jobID = j.jobID " +
            "JOIN JobApplicant ja ON a.applicantID = ja.applicantID " +
            "WHERE j.companyID = " + companyID +
            " AND j.recruiterID = " + recruiterID +
            " AND a.status != 'Rejected'";  
            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            //Button to view detailed information about the interview
            JButton viewBtn = new JButton("View Interview Details");
            viewBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select an interview first.");
                    return;
                }
                showDetailsPopup("Interview Details", extractRow(table, row));
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(viewBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "Interviews Scheduled", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Displays every applicant who has applied to the recruiter's job postings
    private void viewApplicants() {
        try {
            String sql =
                "SELECT a.applicantID, " +
                "a.first_name || ' ' || a.last_name AS \"Applicant\", " +
                "a.industry AS \"Industry\", " +
                "a.email AS \"Email\", " +
                "a.phone AS \"Phone\" " +
                "FROM JobApplicant a " +
                "JOIN JobApplication ja ON a.applicantID = ja.applicantID " +
                "JOIN Job j ON ja.jobID = j.jobID " +
                "WHERE j.recruiterID = " + recruiterID +
                " ORDER BY a.last_name, a.first_name";

            ResultSet rs = dbConnection.executeQuery(sql);
            JTable table = new JTable(readOnlyModel(rs));

            JButton viewBtn = new JButton("View Applicant Details");
            viewBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select an applicant first.");
                    return;
                }
                showDetailsPopup("Applicant Details", extractRow(table, row));
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(viewBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "Applicants", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}