
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.*; 

public class Applicant_GUI extends JPanel{

    private final DBConnection dbConnection; 
    private final String username;
    private final String password;
    private final int applicantID;


    public Applicant_GUI(DB_GUI gui, String username, String password) throws SQLException {
        
        this.dbConnection = DBConnection.getInstance(username, password); 
        this.username = username;
        this.password = password; 
        this.applicantID = gui.getApplicantID();
        
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Applicant Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        Color LightBlue = new Color(173, 216, 230);

        JButton applyJob = new JButton("Apply for a Job");
        JButton viewApps = new JButton("View My Applications");
        JButton viewInterviews = new JButton("View Interviews");
        JButton updateProfile = new JButton("Update Profile");
        JButton exit = new JButton("Exit");

        JButton[] buttons = {applyJob, viewApps, viewInterviews, updateProfile, exit};

        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 20));
            b.setBackground(LightBlue);
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

        /* Button functionality */
        applyJob.addActionListener(e -> openApplyJobWindow());
        viewApps.addActionListener(e -> viewMyApplications());
        viewInterviews.addActionListener(e -> viewMyInterviews());
        updateProfile.addActionListener(e -> openUpdateProfileWindow());
        exit.addActionListener(e -> System.exit(0));
    }

    private void showJobDetails(int jobID) {
        try {
            String sql =
                "SELECT j.jobID, j.title, c.name, j.location, j.salary, j.workingHours, " +
                "TO_CHAR(j.datePosted, 'YYYY-MM-DD'), j.description " +
                "FROM Job j " +
                "JOIN Company c ON j.companyID = c.companyID " +
                "WHERE j.jobID = " + jobID;

            ResultSet rs = dbConnection.executeQuery(sql);

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Error loading job details.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            /* Build job details message */
            String details =
                "Job Title: " + rs.getString(2) + "\n" +
                "Company: " + rs.getString(3) + "\n" +
                "Location: " + rs.getString(4) + "\n" +
                "Salary: $" + rs.getDouble(5) + "/hr\n" +
                "Hours/Week: " + rs.getDouble(6) + "\n" +
                "Date Posted: " + rs.getString(7) + "\n\n" +
                "Description:\n" + rs.getString(8);

            /* Create detail window */
            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));

            int option = JOptionPane.showOptionDialog(this, scrollPane, "Job Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Apply for Job", "Close"}, "Apply for Job");

            if (option == 0) {
                applyForJob(jobID);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* From browseJobs() screen, applicant wants to apply for job they selected */
    private void applyForJob(int jobID) {
        int applicantID = this.applicantID;

        try {
            /* Check if already applied */
            String dupCheckSQL = "SELECT COUNT(*) FROM JobApplication WHERE jobID = ? AND applicantID = ?";
            PreparedStatement dupStmt = dbConnection.getConnection().prepareStatement(dupCheckSQL);
            dupStmt.setInt(1, jobID);
            dupStmt.setInt(2, applicantID);
            ResultSet dupRS = dupStmt.executeQuery();
            dupRS.next();

            if (dupRS.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "You have already applied for this job.", "Duplicate Application", JOptionPane.WARNING_MESSAGE);
                return;
            }

            /* Next jobAppID */
            String nextIDSQL = "SELECT NVL(MAX(jobAppID), 0) + 1 FROM JobApplication";
            ResultSet nextRS = dbConnection.executeQuery(nextIDSQL);
            nextRS.next();
            int nextID = nextRS.getInt(1);

            /* Insert into JobApplication */
            String insertSQL =
                "INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) " +
                "VALUES (?, ?, ?, SYSDATE, 'Submitted')";

            PreparedStatement stmt = dbConnection.getConnection().prepareStatement(insertSQL);
            stmt.setInt(1, nextID);
            stmt.setInt(2, jobID);
            stmt.setInt(3, applicantID);
            stmt.executeUpdate();

            /* Ask user if they want to upload resume */
            int choice = JOptionPane.showConfirmDialog(this, "Application submitted!\nWould you like to upload a resume?", "Upload Resume?", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                uploadResume(applicantID);
            }

            JOptionPane.showMessageDialog(this, "Application complete.", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Applicant accesses Apply for Job screen (from main menu), has to select Job to apply for */
    private void openApplyJobWindow() {
        try {
            String sql =
                "SELECT j.jobID, j.title AS \"Job Title\", c.name AS \"Company\", " +
                "j.location AS \"Location\", j.salary AS \"Salary ($/hr)\", " +
                "j.workingHours AS \"Hours/Week\", TO_CHAR(j.datePosted, 'YYYY-MM-DD') AS \"Date Posted\", j.description " +
                "FROM Job j " +
                "JOIN Company c ON j.companyID = c.companyID " +
                "ORDER BY j.datePosted DESC";

            ResultSet rs = dbConnection.executeQuery(sql);
            DefaultTableModel model = DB_GUI.buildTableModel(rs);

            /* Read-only table model */
            DefaultTableModel readOnlyModel = new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
                @Override public boolean isCellEditable(int r, int c) { return false; }
            };

            JTable table = new JTable(readOnlyModel);

            /* Hide jobID column (index 0) */
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);

            JScrollPane scrollPane = new JScrollPane(table);

            JButton viewBtn = new JButton("View Selected Job");
            viewBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));

            viewBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a job first.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                /* Extract jobID safely */
                Object val = table.getValueAt(row, 0);
                int jobID = (val instanceof BigDecimal)
                        ? ((BigDecimal) val).intValue()
                        : Integer.parseInt(val.toString());

                /* Display job details + apply + upload resume */
                showJobDetails(jobID);
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(viewBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "Apply for Job", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    /* For Jobs table in browseJobs()  */
    private Vector<String> getColumnNames(DefaultTableModel model) {
        Vector<String> names = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            names.add(model.getColumnName(i));
        }
        return names;
    }

    /* For applicants to upload resumes */
    private void uploadResume(int applicantID) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Resume to Upload");

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this, "Resume upload cancelled.");
            return;
        }

        try {
            File file = chooser.getSelectedFile();
            FileInputStream fis = new FileInputStream(file);

            /* Generate next resumeID */
            ResultSet rs = dbConnection.executeQuery(
                "SELECT NVL(MAX(resumeID),0) + 1 FROM Resume"
            );
            rs.next();
            int nextResumeID = rs.getInt(1);

            PreparedStatement ps = dbConnection.getConnection().prepareStatement(
                "INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) " +
                "VALUES (?, ?, ?, SYSDATE)"
            );

            ps.setInt(1, nextResumeID);
            ps.setInt(2, applicantID);
            ps.setBinaryStream(3, fis, (int) file.length());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Resume uploaded successfully!",
                "Upload Complete",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Resume upload failed: " + ex.getMessage(),
                "Upload Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /* View all applications submitted by the applicant */
    private void viewMyApplications() {
        int applicantID = this.applicantID;

        try {
            String sql =
                "SELECT " +
                "    j.title AS \"Job Title\", " +
                "    c.name AS \"Company\", " +
                "    j.location AS \"Location\", " +
                "    a.status AS \"Status\", " +
                "    TO_CHAR(a.dateTime, 'YYYY-MM-DD') AS \"Date Applied\", " +
                "    j.jobID AS jobID /* hidden */ " +
                "FROM JobApplication a " +
                "JOIN Job j ON a.jobID = j.jobID " +
                "JOIN Company c ON j.companyID = c.companyID " +
                "WHERE a.applicantID = ? " +
                "ORDER BY a.dateTime DESC";

            PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
            stmt.setInt(1, applicantID);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = DB_GUI.buildTableModel(rs);

            /* Read-only model */
            DefaultTableModel readOnlyModel =
                new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                };

            JTable table = new JTable(readOnlyModel);
            table.setRowHeight(25);

            /* Hide jobID column (last column) */
            int hiddenCol = table.getColumnCount() - 1;
            table.getColumnModel().getColumn(hiddenCol).setMinWidth(0);
            table.getColumnModel().getColumn(hiddenCol).setMaxWidth(0);

            JScrollPane scrollPane = new JScrollPane(table);

            JButton viewAppBtn = new JButton("View Application");
            viewAppBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));

            viewAppBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select an application first.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                /* Extract jobID */
                Object val = table.getValueAt(row, hiddenCol);
                int jobID = (val instanceof java.math.BigDecimal)
                        ? ((java.math.BigDecimal) val).intValue()
                        : Integer.parseInt(val.toString());

                String jobTitle = table.getValueAt(row, 0).toString();
                String company = table.getValueAt(row, 1).toString();
                String location = table.getValueAt(row, 2).toString();
                String status = table.getValueAt(row, 3).toString();
                String dateApplied = table.getValueAt(row, 4).toString();

                showApplicationDetails(jobID, jobTitle, company, location, status, dateApplied);
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(viewAppBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(
                this,
                panel,
                "My Applications",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Shows the selected job application details */
    private void showApplicationDetails(int jobID, String jobTitle, String company, String location, String status, String dateApplied) {

        JTextArea text = new JTextArea(
            "Job Title: " + jobTitle + "\n" +
            "Company: " + company + "\n" +
            "Location: " + location + "\n" +
            "Status: " + status + "\n" +
            "Date Applied: " + dateApplied + "\n"
        );

        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        JButton viewJobBtn = new JButton("View Job Posting");
        viewJobBtn.setFont(new Font("Times New Roman", Font.BOLD, 16));

        viewJobBtn.addActionListener(ev -> showJobDetails(jobID));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(viewJobBtn, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Application Details", JOptionPane.PLAIN_MESSAGE);
    }

    /* View interviews scheduled for the applicant */
    private void viewMyInterviews() {
        int applicantID = this.applicantID;

        try {
            String sql =
                "SELECT " +
                "    j.title AS \"Job Title\", " +
                "    c.name AS \"Company\", " +
                "    i.location AS \"Interview Location\", " +
                "    TO_CHAR(i.dateTime, 'YYYY-MM-DD HH24:MI') AS \"Interview Date\", " +
                "    a.status AS \"Application Status\", " +
                "    j.jobID AS jobID /* hidden */ " +
                "FROM Interview i " +
                "JOIN JobApplication a ON i.jobAppID = a.jobAppID " +
                "JOIN Job j ON a.jobID = j.jobID " +
                "JOIN Company c ON j.companyID = c.companyID " +
                "WHERE a.applicantID = ? " +
                "ORDER BY i.dateTime ASC";

            PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
            stmt.setInt(1, applicantID);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = DB_GUI.buildTableModel(rs);

            /* Read-only model */
            DefaultTableModel readOnlyModel =
                new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
                    @Override public boolean isCellEditable(int r, int c) { return false; }
                };

            JTable table = new JTable(readOnlyModel);
            table.setRowHeight(25);

            /* Hide jobID column (last column) */
            int hiddenCol = table.getColumnCount() - 1;
            table.getColumnModel().getColumn(hiddenCol).setMinWidth(0);
            table.getColumnModel().getColumn(hiddenCol).setMaxWidth(0);

            JScrollPane scrollPane = new JScrollPane(table);

            JButton viewBtn = new JButton("View Interview");
            viewBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));

            viewBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select an interview first.", "Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                /* Extract jobID */
                Object val = table.getValueAt(row, hiddenCol);
                int jobID = (val instanceof java.math.BigDecimal)
                        ? ((java.math.BigDecimal) val).intValue()
                        : Integer.parseInt(val.toString());

                String title = table.getValueAt(row, 0).toString();
                String company = table.getValueAt(row, 1).toString();
                String location = table.getValueAt(row, 2).toString();
                String date = table.getValueAt(row, 3).toString();
                String status = table.getValueAt(row, 4).toString();

                showInterviewDetails(jobID, title, company, location, date, status);
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(viewBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "My Interviews", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Shows the selected interview details */
    private void showInterviewDetails(int jobID, String jobTitle, String company, String location, String date, String status) {

        JTextArea text = new JTextArea(
            "Job Title: " + jobTitle + "\n" +
            "Company: " + company + "\n" +
            "Interview Location: " + location + "\n" +
            "Interview Date: " + date + "\n" +
            "Application Status: " + status + "\n"
        );

        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        JButton viewJobBtn = new JButton("View Job Posting");
        viewJobBtn.setFont(new Font("Times New Roman", Font.BOLD, 16));

        viewJobBtn.addActionListener(ev -> showJobDetails(jobID));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(viewJobBtn, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Interview Details", JOptionPane.PLAIN_MESSAGE);
    }

    /* Allows applicant to update their profile information */
    private void openUpdateProfileWindow() {
        int applicantID = this.applicantID;

        try {
            /* Fetch current data */
            String sql =
                "SELECT first_name, last_name, industry, " +
                "TO_CHAR(birthdate, 'YYYY-MM-DD'), address, email, phone " +
                "FROM JobApplicant WHERE applicantID = ?";

            PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
            stmt.setInt(1, applicantID);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Applicant profile not found.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            /* Build form fields */
            JTextField firstField = new JTextField(rs.getString(1), 20);
            JTextField lastField = new JTextField(rs.getString(2), 20);
            JTextField industryField = new JTextField(rs.getString(3), 20);
            JTextField birthField = new JTextField(rs.getString(4), 20);
            JTextField addressField = new JTextField(rs.getString(5), 20);
            JTextField emailField = new JTextField(rs.getString(6), 20);
            JTextField phoneField = new JTextField(rs.getString(7), 20);

            JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
            form.add(new JLabel("First Name:"));   form.add(firstField);
            form.add(new JLabel("Last Name:"));    form.add(lastField);
            form.add(new JLabel("Industry:"));     form.add(industryField);
            form.add(new JLabel("Birthdate (YYYY-MM-DD):")); form.add(birthField);
            form.add(new JLabel("Address:"));      form.add(addressField);
            form.add(new JLabel("Email:"));        form.add(emailField);
            form.add(new JLabel("Phone:"));        form.add(phoneField);

            int option = JOptionPane.showOptionDialog(
                this,
                form,
                "Update Profile",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Save Changes", "Cancel"},
                "Save Changes"
            );

            if (option == 0) {
                updateApplicantProfile(
                    applicantID,
                    firstField.getText().trim(),
                    lastField.getText().trim(),
                    industryField.getText().trim(),
                    birthField.getText().trim(),
                    addressField.getText().trim(),
                    emailField.getText().trim(),
                    phoneField.getText().trim()
                );
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading profile: " + ex.getMessage(),
                    "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Saves updated profile information to the database */
    private void updateApplicantProfile(int applicantID, String first, String last, String industry, String birth, String address, String email, String phone) {

        /* Basic validation */
        if (first.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String sql =
                "UPDATE JobApplicant " +
                "SET first_name=?, last_name=?, industry=?, birthdate=TO_DATE(?, 'YYYY-MM-DD'), " +
                "address=?, email=?, phone=? " +
                "WHERE applicantID=?";

            PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql);
            stmt.setString(1, first);
            stmt.setString(2, last);
            stmt.setString(3, industry);
            stmt.setString(4, birth);
            stmt.setString(5, address);
            stmt.setString(6, email);
            stmt.setString(7, phone);
            stmt.setInt(8, applicantID);

            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
