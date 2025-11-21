
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.sql.*;
import java.util.*; 

public class Applicant_GUI extends  JPanel{

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

        /* Button functionality */
        browseJobs.addActionListener(e -> browseJobs());
        applyJob.addActionListener(e -> openApplyJobWindow());
        // viewApps.addActionListener(e -> viewMyApplications());
        // viewInterviews.addActionListener(e -> viewMyInterviews());
        // updateProfile.addActionListener(e -> updateMyProfile());
        exit.addActionListener(e -> System.exit(0));
    }

    private void browseJobs() {
        try {
            String sql =
                "SELECT j.jobID, j.title AS \"Job Title\", c.name AS \"Company\", j.location AS \"Location\", " +
                "j.salary AS \"Salary ($/hr)\", j.workingHours AS \"Hours/Week\", " +
                "TO_CHAR(j.datePosted, 'YYYY-MM-DD') AS \"Date Posted\", j.description AS \"Description\" " +
                "FROM Job j " +
                "JOIN Company c ON j.companyID = c.companyID " +
                "ORDER BY j.datePosted DESC";

            ResultSet rs = dbConnection.executeQuery(sql);
            DefaultTableModel model = DB_GUI.buildTableModel(rs);

            /* Read-only */
            Vector<String> columnNames = new Vector<>();
            for (int i = 0; i < model.getColumnCount(); i++) {
                columnNames.add(model.getColumnName(i));
            }

            DefaultTableModel readOnlyModel = new DefaultTableModel(model.getDataVector(), columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };

            JTable table = new JTable(readOnlyModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.setRowHeight(25);

            /* Hide jobID column */
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(0).setWidth(0);

            /* Create a panel for the table + buttons */
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout());

            JButton viewButton = new JButton("View Selected Job");
            JButton closeButton = new JButton("Close");

            buttonPanel.add(viewButton);
            buttonPanel.add(closeButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            /* Create a dialog */
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Available Jobs", true);
            dialog.setContentPane(panel);
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(this);

            /* Handle button click */
            viewButton.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a job first.");
                    return;
                }
                
                Object value = table.getValueAt(row, 0);
                int jobID;

                if (value instanceof Integer) {
                    jobID = (Integer) value;
                } else if (value instanceof BigDecimal) {
                    jobID = ((BigDecimal) value).intValue();
                } else {
                    throw new RuntimeException("Unknown ID type: " + value.getClass());
                }

                dialog.dispose();
                showJobDetails(jobID);
            });

            /* Handle double-click */
            table.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    if (evt.getClickCount() == 2) {
                        int row = table.getSelectedRow();
                        if (row != -1) {
                            int jobID = (int) table.getValueAt(row, 0);
                            dialog.dispose();
                            showJobDetails(jobID);
                        }
                    }
                }
            });

            /* Close button */
            closeButton.addActionListener(ev -> dialog.dispose());

            dialog.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

            int option = JOptionPane.showOptionDialog(
                this,
                scrollPane,
                "Job Details",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Apply for Job", "Close"},
                "Apply for Job"
            );

            if (option == 0) {
                applyForJob(jobID);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* From browseJobs() screen, applicant wants to apply for job they selected */
    // private void applyForJob(int jobID) {
    //     int applicantID = this.applicantID;

    //     try {
    //         /* Check if already applied */
    //         String dupCheckSQL = "SELECT COUNT(*) FROM JobApplication WHERE jobID = ? AND applicantID = ?";
    //         PreparedStatement dupStmt = dbConnection.getConnection().prepareStatement(dupCheckSQL);
    //         dupStmt.setInt(1, jobID);
    //         dupStmt.setInt(2, applicantID);
    //         ResultSet dupRS = dupStmt.executeQuery();
    //         dupRS.next();

    //         if (dupRS.getInt(1) > 0) {
    //             JOptionPane.showMessageDialog(this,
    //                 "You have already applied for this job.",
    //                 "Duplicate Application",
    //                 JOptionPane.WARNING_MESSAGE
    //             );
    //             return;
    //         }

    //         /* Next jobAppID */
    //         String nextIDSQL = "SELECT NVL(MAX(jobAppID), 0) + 1 FROM JobApplication";
    //         ResultSet nextRS = dbConnection.executeQuery(nextIDSQL);
    //         nextRS.next();
    //         int nextID = nextRS.getInt(1);

    //         /* Insert */
    //         String insertSQL =
    //             "INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) " +
    //             "VALUES (?, ?, ?, SYSDATE, 'Submitted')";

    //         PreparedStatement stmt = dbConnection.getConnection().prepareStatement(insertSQL);
    //         stmt.setInt(1, nextID);
    //         stmt.setInt(2, jobID);
    //         stmt.setInt(3, applicantID);
    //         stmt.executeUpdate();

    //         JOptionPane.showMessageDialog(this,
    //             "Application submitted!",
    //             "Success", JOptionPane.INFORMATION_MESSAGE);

    //     } catch (SQLException ex) {
    //         JOptionPane.showMessageDialog(this,
    //             "Error: " + ex.getMessage(),
    //             "SQL Error", JOptionPane.ERROR_MESSAGE);
    //     }
    // }

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
                JOptionPane.showMessageDialog(this,
                    "You have already applied for this job.",
                    "Duplicate Application",
                    JOptionPane.WARNING_MESSAGE
                );
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
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Application submitted!\nWould you like to upload a resume?",
                    "Upload Resume?",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                uploadResume(applicantID);
            }

            JOptionPane.showMessageDialog(this,
                "Application complete.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "SQL Error",
                JOptionPane.ERROR_MESSAGE);
        }
}


    /* Applicant accesses Apply for Job screen (from main menu), has to select Job to apply for */
    // private void openApplyJobWindow() {
    //     try {
    //         String sql =
    //             "SELECT j.jobID, j.title AS \"Job Title\", c.name AS \"Company\", " +
    //             "j.location AS \"Location\", j.salary AS \"Salary ($/hr)\", " +
    //             "j.workingHours AS \"Hours/Week\", TO_CHAR(j.datePosted, 'YYYY-MM-DD') AS \"Date Posted\" " +
    //             "FROM Job j " +
    //             "JOIN Company c ON j.companyID = c.companyID " +
    //             "ORDER BY j.datePosted DESC";

    //         ResultSet rs = dbConnection.executeQuery(sql);
    //         DefaultTableModel model = DB_GUI.buildTableModel(rs);

    //         /* Make model read-only */
    //         DefaultTableModel readOnlyModel = new DefaultTableModel(model.getDataVector(), getColumnNames(model)) {
    //             @Override public boolean isCellEditable(int r, int c) { return false; }
    //         };

    //         JTable table = new JTable(readOnlyModel);

    //         /* Hide jobID column */
    //         table.getColumnModel().getColumn(0).setMinWidth(0);
    //         table.getColumnModel().getColumn(0).setMaxWidth(0);

    //         JScrollPane scrollPane = new JScrollPane(table);

    //         JButton applyBtn = new JButton("Apply for Selected Job");
    //         applyBtn.setFont(new Font("Times New Roman", Font.BOLD, 18));

    //         applyBtn.addActionListener(ev -> {
    //             int row = table.getSelectedRow();
    //             if (row == -1) {
    //                 JOptionPane.showMessageDialog(this, "Select a job first.", "Error", JOptionPane.WARNING_MESSAGE);
    //                 return;
    //             }

    //             /* Extract jobID safely */
    //             Object val = table.getValueAt(row, 0);
    //             int jobID = (val instanceof BigDecimal)
    //                     ? ((BigDecimal) val).intValue()
    //                     : Integer.parseInt(val.toString());

    //             applyForJob(jobID);
    //         });

    //         JPanel panel = new JPanel(new BorderLayout());
    //         panel.add(scrollPane, BorderLayout.CENTER);
    //         panel.add(applyBtn, BorderLayout.SOUTH);

    //         JOptionPane.showMessageDialog(this, panel, "Apply for Job", JOptionPane.PLAIN_MESSAGE);

    //     } catch (SQLException ex) {
    //         JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
    //     }
    // }

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


}
