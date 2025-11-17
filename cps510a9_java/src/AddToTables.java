import javax.swing.*;
import java.awt.*;
import java.sql.*; 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AddToTables extends JPanel {

    private final DBConnection dbConnection;

    public AddToTables(DB_GUI gui, String username, String password) throws SQLException {

        // setting up the GUI
        this.dbConnection = DBConnection.getInstance(username, password);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Add To Tables", SwingConstants.CENTER);
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

        String query1 = "SELECT * FROM Company";
        String query2 = "SELECT * FROM Recruiter";
        String query3 = "SELECT * FROM Job";
        String query4 = "SELECT * FROM JobApplicant";
        String query5 = "SELECT * FROM JobApplication";
        String query6 = "SELECT * FROM Resume";
        String query7 = "SELECT * FROM Interview";

        // attach the buttons to their functions, pass in the select * query to show updated table after insertion
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
    // again, the other ones basically do the exact same thing as this one, but customised for their respective tables with variable names, strings, etc.
    private void attachCompanyButton(JButton button, String query) {
        button.addActionListener(e -> { // attach this functionality to the button
            try {

                // create text fields for each attribute in the table
                JTextField companyID = new JTextField();
                JTextField name = new JTextField();
                JTextField industry = new JTextField();
                JTextField location = new JTextField();
                JTextField email = new JTextField();
                JTextField phone = new JTextField();

                // pass it into a dialog/input box
                Object[] queryValues = {
                    "Company ID (mandatory int, unique):", companyID,
                    "Name (mandatory, unique):", name,
                    "Industry:", industry,
                    "Location:", location,
                    "Email:", email,
                    "Phone:", phone
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Insert New Company", JOptionPane.OK_CANCEL_OPTION);

                // when the dialogue is confirmed - assuming there are values in the input fields
                if (option == JOptionPane.OK_OPTION) {

                    //prepare an sql statement
                    String insertSQL = "INSERT INTO Company (companyID, name, industry, location, email, phone) VALUES (?, ?, ?, ?, ?, ?)"; 
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL); // 

                    // the following here go into the question marks of the prepared statement in order, basically
                    // 1 for the first question mark, 2 for the 2nd, etc.
                    pstmt.setInt(1, Integer.parseInt(companyID.getText()));
                    pstmt.setString(2, name.getText());
                    pstmt.setString(3, industry.getText());
                    pstmt.setString(4, location.getText());
                    pstmt.setString(5, email.getText());
                    pstmt.setString(6, phone.getText());

                    int rowsAffected = pstmt.executeUpdate(); // how many rows were affected?
                    if (rowsAffected > 0) { // if more than 0, you did it
                        JOptionPane.showMessageDialog(null, "New company added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else { // else something went wrong
                        JOptionPane.showMessageDialog(null, "Failed to add new company.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) { // catch any sql errors - database end
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // catch any other errors - java end (usually careless input)
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
                    "RecruiterID (mandatory int, unique):", recruiterID,
                    "CompanyID (mandatory, reference to Company table):", companyID,
                    "First Name:", first_name,
                    "Last Name:", last_name,
                    "Email:", email,
                    "Phone:", phone
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Insert New Recruiter", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "INSERT INTO Recruiter (recruiterID, companyID, first_name, last_name, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(1, Integer.parseInt(recruiterID.getText()));
                    pstmt.setInt(2, Integer.parseInt(companyID.getText()));
                    pstmt.setString(3, first_name.getText());
                    pstmt.setString(4, last_name.getText());
                    pstmt.setString(5, email.getText());
                    pstmt.setString(6, phone.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "New recruiter added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add new recruiter.", "Error", JOptionPane.ERROR_MESSAGE);
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
                    "JobID (mandatory int, unique):", jobID,
                    "CompanyID (mandatory, reference to Company table):", companyID,
                    "RecruiterID (mandatory, reference to Recruiter table):", recruiterID,
                    "Salary:", salary,
                    "Working Hours: ", workingHours,
                    "Date Posted (YYYY-MM-DD):", datePosted,
                    "Location:", location,
                    "Title (mandatory):", title,
                    "Description (mandatory):", description
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Insert New Job", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "INSERT INTO Job (jobID, companyID, recruiterID, salary, workingHours, datePosted, location, title, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(1, Integer.parseInt(jobID.getText()));
                    pstmt.setInt(2, Integer.parseInt(companyID.getText()));
                    pstmt.setInt(3, Integer.parseInt(recruiterID.getText()));
                    pstmt.setFloat(4, salary.getText() == null || salary.getText().isEmpty() ? 0 : Float.parseFloat(salary.getText()));
                    pstmt.setFloat(5, workingHours.getText() == null || workingHours.getText().isEmpty() ? 0 : Float.parseFloat(workingHours.getText()));
                    pstmt.setDate(6, Date.valueOf(datePosted.getText()));
                    pstmt.setString(7, location.getText());
                    pstmt.setString(8, title.getText());
                    pstmt.setString(9, description.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "New job added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add new job.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                    "ApplicantID (mandatory int, unique):", applicantID,
                    "First Name (mandatory):", first_name,
                    "Last Name:", last_name,
                    "Industry:", industry,
                    "Birthdate (YYYY-MM-DD):", birthDate,
                    "Address:", address,
                    "Email:", email,
                    "Phone:", phone
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Insert New Job Applicant", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "INSERT INTO JobApplicant (applicantID, first_name, last_name, industry, birthDate, address, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(1, Integer.parseInt(applicantID.getText()));
                    pstmt.setString(2, first_name.getText());
                    pstmt.setString(3, last_name.getText());
                    pstmt.setString(4, industry.getText());
                    pstmt.setDate(5, Date.valueOf(birthDate.getText()));
                    pstmt.setString(6, address.getText());
                    pstmt.setString(7, email.getText());
                    pstmt.setString(8, phone.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "New job applicant added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add new job applicant.", "Error", JOptionPane.ERROR_MESSAGE);
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
                    "JobAppID (mandatory int, unique):", jobAppID,
                    "JobID (mandatory, reference to Job table):", jobID,
                    "ApplicantID (mandatory, reference to JobApplicant table):", applicantID,
                    "Date (YYYY-MM-DD):", dateTime,
                    "Status:", status
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Insert New Job Application", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "INSERT INTO JobApplication (jobAppID, jobID, applicantID, dateTime, status) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(1, Integer.parseInt(jobAppID.getText()));
                    pstmt.setInt(2, Integer.parseInt(jobID.getText()));
                    pstmt.setInt(3, Integer.parseInt(applicantID.getText()));
                    pstmt.setDate(4, Date.valueOf(dateTime.getText()));
                    pstmt.setString(5, status.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "New job application added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add new job application.", "Error", JOptionPane.ERROR_MESSAGE);
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

                // same as before, input dialog with the fields
                Object[] queryValues = {
                    "ResumeID (mandatory int, unique):", resumeID,
                    "ApplicantID (mandatory, reference to JobApplicant table):", applicantID,
                    "Upload File (enter file path):", pickFileButton,
                    "Upload Date (YYYY-MM-DD):", uploadDate
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Insert New Resume", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "INSERT INTO Resume (resumeID, applicantID, uploadFile, uploadDate) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    // the following here go into the question marks of the prepared statem in order, basically
                    pstmt.setInt(1, Integer.parseInt(resumeID.getText()));
                    pstmt.setInt(2, Integer.parseInt(applicantID.getText()));
                    pstmt.setBlob(3, convertFileToInputStream(pickFileButton.getText().equals("Pick a file") ? null : pickFileButton.getText())); // this part - use a helper function to convert the fileName from the text button to an input stream
                    pstmt.setDate(4, Date.valueOf(uploadDate.getText()));

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "New resume added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add new resume.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // the helper function in question - converts a filepath/filename into a FileInputStream for the blob insertion
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
                    "InterviewID (mandatory int, unique):", interviewID,
                    "JobAppID (mandatory, reference to JobApplication table):", jobAppID,
                    "Date (YYYY-MM-DD):", dateTime,
                    "Location:", location
                };

                int option = JOptionPane.showConfirmDialog(null, queryValues, "Insert New Interview", JOptionPane.OK_CANCEL_OPTION);

                if (option == JOptionPane.OK_OPTION) {
                    String insertSQL = "INSERT INTO Interview (interviewID, jobAppID, dateTime, location) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(insertSQL);
                    pstmt.setInt(1, Integer.parseInt(interviewID.getText()));
                    pstmt.setInt(2, Integer.parseInt(jobAppID.getText()));
                    pstmt.setDate(3, Date.valueOf(dateTime.getText()));
                    pstmt.setString(4, location.getText());

                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "New interview added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add new interview.", "Error", JOptionPane.ERROR_MESSAGE);
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