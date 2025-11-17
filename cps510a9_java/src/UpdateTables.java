import javax.swing.*;
import java.awt.*;
import java.sql.*; 
import java.io.File;
import java.io.FileInputStream;

public class UpdateTables extends JPanel {

    private final DBConnection dbConnection;

    public UpdateTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password); // connect to the DB

        // setting up the GUI
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Update Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton t1 = new JButton("Company Table");
        JButton t2 = new JButton("Recruiter Table");
        JButton t3 = new JButton("Job Table");
        JButton t4 = new JButton("JobApplicant Table");
        JButton t5 = new JButton("JobApplication Table");
        JButton t6 = new JButton("Resume Table");
        JButton t7 = new JButton("Interview Table");

        JButton[] buttons = {t1, t2, t3, t4, t5, t6, t7};
        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 18));
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());
        add(backButton, BorderLayout.SOUTH);

        // get subqueries to show tables after button execution
        String query1 = "SELECT * FROM Company";
        String query2 = "SELECT * FROM Recruiter";
        String query3 = "SELECT * FROM Job";
        String query4 = "SELECT * FROM JobApplicant";
        String query5 = "SELECT * FROM JobApplication";
        String query6 = "SELECT * FROM Resume";
        String query7 = "SELECT * FROM Interview";

        // attach specific functionality to each button
        attachCompanyButton(t1, query1);
        attachRecruiterButton(t2, query2);
        attachJobButton(t3, query3);
        attachJobApplicantButton(t4, query4);
        attachJobApplicationButton(t5, query5);
        attachResumeButton(t6, query6);
        attachInterviewButton(t7, query7);
    }

    // function that provides specific functionality for the Company button
    // these ones mostly follow the same format, just different fields - i'll just comment this one in detail
    // this is essentially "do this when the button is pressed"
    // again, the other ones basically do the exact same thing as this one, but customised for their respective tables with variable names, strings, etc.
    private void attachCompanyButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {

                // create text fields for each column in the Company table
                JTextField companyID = new JTextField();
                JTextField name = new JTextField();
                JTextField industry = new JTextField();
                JTextField location = new JTextField();
                JTextField email = new JTextField();
                JTextField phone = new JTextField();

                // pass it into a dialog/input box - companyID is the identifier for which row to update
                Object[] queryValues = {
                    "Company ID to update (match this to an existing company):", companyID,
                    "New Name (mandatory, unique):", name,
                    "New Industry:", industry,
                    "New Location:", location,
                    "New Email:", email,
                    "New Phone:", phone
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Update Company", JOptionPane.OK_CANCEL_OPTION);

                // if user clicked OK, proceed with the update
                if (option == JOptionPane.OK_OPTION) {

                    // prepare an SQL statement updating the table by companyID
                    String insertSQL = "UPDATE Company SET name = ?, industry = ?, location = ?, email = ?, phone = ? WHERE companyID = ?"; 
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);

                    // the following here go into the question marks of the prepared statem in order, basically
                    pstmt.setInt(6, Integer.parseInt(companyID.getText()));
                    pstmt.setString(1, name.getText());
                    pstmt.setString(2, industry.getText());
                    pstmt.setString(3, location.getText());
                    pstmt.setString(4, email.getText());
                    pstmt.setString(5, phone.getText());

                    int rowsAffected = pstmt.executeUpdate(); // do it
                    if (rowsAffected > 0) { // if something was changed
                        JOptionPane.showMessageDialog(null, "Company updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else { // nothing was changed, something was off
                        JOptionPane.showMessageDialog(null, "Failed to update company.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) { // catch database-side errors
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // catch other errors - usually careless input
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void attachRecruiterButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {

                JTextField recruiterID = new JTextField();
                JTextField companyID = new JTextField();
                JTextField first_name = new JTextField();
                JTextField last_name = new JTextField();
                JTextField email = new JTextField();
                JTextField phone = new JTextField();

                Object[] queryValues = {
                    "RecruiterID to update (match this to an existing recruiter):", recruiterID,
                    "New CompanyID (mandatory, reference to Company table):", companyID,
                    "New First Name:", first_name,
                    "New Last Name:", last_name,
                    "New Email:", email,
                    "New Phone:", phone
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Update Recruiter", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "UPDATE Recruiter SET companyID = ?, first_name = ?, last_name = ?, email = ?, phone = ? WHERE recruiterID = ?";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(6, Integer.parseInt(recruiterID.getText()));
                    pstmt.setInt(1, Integer.parseInt(companyID.getText()));
                    pstmt.setString(2, first_name.getText());
                    pstmt.setString(3, last_name.getText());
                    pstmt.setString(4, email.getText());
                    pstmt.setString(5, phone.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Recruiter updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update recruiter.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void attachJobButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {

                JTextField jobID = new JTextField();
                JTextField companyID = new JTextField();
                JTextField recruiterID = new JTextField();
                JTextField salary = new JTextField();
                JTextField workingHours = new JTextField();    
                JTextField datePosted = new JTextField();
                JTextField location = new JTextField();
                JTextField title = new JTextField();
                JTextField description = new JTextField();

                Object[] queryValues = {
                    "JobID to update (match this to the existing job):", jobID,
                    "New CompanyID (mandatory, reference to Company table):", companyID,
                    "New RecruiterID (mandatory, reference to Recruiter table):", recruiterID,
                    "New Salary:", salary,
                    "New Working Hours: ", workingHours,
                    "New Date Posted (YYYY-MM-DD):", datePosted,
                    "New Location:", location,
                    "New Title (mandatory):", title,
                    "New Description (mandatory):", description
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Update Job", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "UPDATE Job SET companyID = ?, recruiterID = ?, salary = ?, workingHours = ?, datePosted = ?, location = ?, title = ?, description = ? WHERE jobID = ?";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(9, Integer.parseInt(jobID.getText()));
                    pstmt.setInt(1, Integer.parseInt(companyID.getText()));
                    pstmt.setInt(2, Integer.parseInt(recruiterID.getText()));
                    pstmt.setFloat(3, salary.getText() == null || salary.getText().isEmpty() ? 0 : Float.parseFloat(salary.getText()));
                    pstmt.setFloat(4, workingHours.getText() == null || workingHours.getText().isEmpty() ? 0 : Float.parseFloat(workingHours.getText()));
                    pstmt.setDate(5, Date.valueOf(datePosted.getText()));
                    pstmt.setString(6, location.getText());
                    pstmt.setString(7, title.getText());
                    pstmt.setString(8, description.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Job updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update job.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void attachJobApplicantButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {

                JTextField applicantID = new JTextField();
                JTextField first_name = new JTextField();
                JTextField last_name = new JTextField();
                JTextField industry = new JTextField();
                JTextField birthDate = new JTextField();
                JTextField address = new JTextField();
                JTextField email = new JTextField();
                JTextField phone = new JTextField();

                Object[] queryValues = {
                    "ApplicantID to update (match this to an existing applicant):", applicantID,
                    "New First Name (mandatory):", first_name,
                    "New Last Name:", last_name,
                    "New Industry:", industry,
                    "New Birthdate (YYYY-MM-DD):", birthDate,
                    "New Address:", address,
                    "New Email:", email,
                    "New Phone:", phone
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Update Job Applicant", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "UPDATE JobApplicant SET first_name = ?, last_name = ?, industry = ?, birthdate = ?, address = ?, email = ?, phone = ? WHERE applicantID = ?";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(8, Integer.parseInt(applicantID.getText()));
                    pstmt.setString(1, first_name.getText());
                    pstmt.setString(2, last_name.getText());
                    pstmt.setString(3, industry.getText());
                    pstmt.setDate(4, Date.valueOf(birthDate.getText()));
                    pstmt.setString(5, address.getText());
                    pstmt.setString(6, email.getText());
                    pstmt.setString(7, phone.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Job applicant updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update job applicant.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void attachJobApplicationButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {

                JTextField jobAppID = new JTextField();
                JTextField jobID = new JTextField();
                JTextField applicantID = new JTextField();
                JTextField dateTime = new JTextField();
                JTextField status = new JTextField();

                Object[] queryValues = {
                    "JobAppID to update (match this to an existing job application):", jobAppID,
                    "New JobID (mandatory, reference to Job table):", jobID,
                    "New ApplicantID (mandatory, reference to JobApplicant table):", applicantID,
                    "New Date (YYYY-MM-DD):", dateTime,
                    "New Status:", status
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Update Job Application", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "UPDATE JobApplication SET jobID = ?, applicantID = ?, dateTime = ?, status = ? WHERE jobAppID = ?";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(5, Integer.parseInt(jobAppID.getText()));
                    pstmt.setInt(1, Integer.parseInt(jobID.getText()));
                    pstmt.setInt(2, Integer.parseInt(applicantID.getText()));
                    pstmt.setDate(3, Date.valueOf(dateTime.getText()));
                    pstmt.setString(4, status.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Job application updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update job application.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // this one is slightly different because of the blob file upload
    private void attachResumeButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {

                JTextField resumeID = new JTextField();
                JTextField applicantID = new JTextField();
                JButton pickFileButton = new JButton("Pick a file"); // this has changed to a button that when clicked, opens a file picker
                JTextField uploadDate = new JTextField();

                // here's that button's functionality, and the filepicker associated with it
                // variable scope prevents us from accessing the file picked from inside of here
                // so we access the file name through the button text later on
                pickFileButton.addActionListener (event -> {
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        fileChooser.setSelectedFile(selectedFile);
                        pickFileButton.setText(selectedFile.getAbsolutePath());
                    }
                });

                // same deal as before, pass in the text fields into a dialog
                Object[] queryValues = {
                    "ResumeID to update (match this to an existing resume):", resumeID,
                    "New ApplicantID (mandatory, reference to JobApplicant table):", applicantID,
                    "New Upload File (enter file path):", pickFileButton,
                    "New Upload Date (YYYY-MM-DD):", uploadDate
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Update Resume", JOptionPane.OK_CANCEL_OPTION);

                // when clicking ok, everything remains the same except...
                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "UPDATE Resume SET applicantID = ?, uploadFile = ?, uploadDate = ? WHERE resumeID = ?";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(4, Integer.parseInt(resumeID.getText()));
                    pstmt.setInt(1, Integer.parseInt(applicantID.getText()));
                    pstmt.setBlob(2, convertFileToInputStream(pickFileButton.getText().equals("Pick a file") ? null : pickFileButton.getText())); // this part - we pass in a file input stream created from the selected file using a helper
                    pstmt.setDate(3, Date.valueOf(uploadDate.getText()));

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Resume updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update resume - no rows affected.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // helper function converting a filepath into a fileinput stream
    // we use this to help with the resume blob upload
    private FileInputStream convertFileToInputStream(String fileName)  {
        File file = new File(fileName);
        try {
            return new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error reading file:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void attachInterviewButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {

                JTextField interviewID = new JTextField();
                JTextField jobAppID = new JTextField();
                JTextField dateTime = new JTextField();
                JTextField location = new JTextField();

                Object[] queryValues = {
                    "InterviewID to update (match this to an existing interview):", interviewID,
                    "New JobAppID (mandatory, reference to JobApplication table):", jobAppID,
                    "New Date (YYYY-MM-DD):", dateTime,
                    "New Location:", location
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Update Interview", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "UPDATE Interview SET jobAppID = ?, dateTime = ?, location = ? WHERE interviewID = ?";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    // the following here go into the question marks of the prepared statem in order, basically
                    pstmt.setInt(4, Integer.parseInt(interviewID.getText()));
                    pstmt.setInt(1, Integer.parseInt(jobAppID.getText()));
                    pstmt.setDate(2, Date.valueOf(dateTime.getText()));
                    pstmt.setString(3, location.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Interview updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update interview.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

}