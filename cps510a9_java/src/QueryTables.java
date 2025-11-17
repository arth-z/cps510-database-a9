import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class QueryTables extends JPanel {

    private final DBConnection dbConnection;

    public QueryTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);

        setLayout(new BorderLayout());

//Label for the title 
        JLabel title = new JLabel("Query Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

//Create the grid like panel for the query menu
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));

//Create the buttons for each query
        JButton q1 = new JButton("Companies with posted jobs but no interviews");
        JButton q2 = new JButton("Companies above average Working hours");
        JButton q3 = new JButton("Recruiters and applicants connected to Apple Canada");
        JButton q4 = new JButton("Companies with applications under review");
        JButton q5 = new JButton("Applicants who have never Been interviewed");
        JButton q6 = new JButton("Job applicants who applied to either Apple Canada or AMD");
        JButton q7 = new JButton("Total job applications per company and show only those above average");
        JButton q8 = new JButton("Companies that have at least one job posting where at least one applicant has applied");
        JButton q9 = new JButton("Job applicants that have applied for at least one job, but have never been interviewed");
        JButton q10 = new JButton("Average salary of all jobs posted, including the minimum and maximum salaries of the jobs posted by the company");

        //Add the buttons to the panel and style the font
        JButton[] buttons = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 18));
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

        //Implement the back button to go to the main menu
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(e -> gui.showMainMenu());
        add(backButton, BorderLayout.SOUTH);

        String query1 =
                "SELECT c.name as CompanyName " +
                "FROM Company c, Job j " +
                "WHERE c.companyID = j.companyID " +
                "MINUS " +
                "SELECT c.name as CompanyName " +
                "FROM Company c, Job j, JobApplication japp, Interview i " +
                "WHERE c.companyID = j.companyID AND j.jobID = japp.jobID AND japp.jobAppID = i.jobAppID";

        String query2 =
                "SELECT c.name as CompanyName, AVG(j.workingHours) AS AverageWorkingHours, COUNT(j.jobID) as TotalJobs " +
                "FROM Company c, Job j " +
                "WHERE c.companyID = j.companyID " +
                "GROUP BY c.name " +
                "HAVING AVG(j.workingHours) > (SELECT AVG(workingHours) FROM Job)";

        String query3 =
                "SELECT r.last_name, r.first_name, r.email " +
                "FROM RECRUITER r, Company c " +
                "WHERE r.companyID = c.companyID AND c.name = 'Apple Canada' " +
                "UNION " +
                "SELECT ja.last_name, ja.first_name, ja.email " +
                "FROM JobApplicant ja, JobApplication japp, Job j, Company c " +
                "WHERE c.name = 'Apple Canada' AND ja.applicantID = japp.applicantID AND japp.jobID = j.jobID AND j.companyID = c.companyID";

        String query4 =
                "SELECT c.name AS CompanyName " +
                "FROM Company c " +
                "WHERE EXISTS ( " +
                "    SELECT * FROM Job j, JobApplication japp " +
                "    WHERE j.companyID = c.companyID AND japp.jobID = j.jobID AND japp.status = 'Under Review')";

        String query5 =
                "SELECT ja.last_name, ja.first_name, ja.email " +
                "FROM JobApplicant ja " +
                "WHERE NOT EXISTS ( " +
                "    SELECT * FROM JobApplication japp, Interview i " +
                "    WHERE japp.applicantID = ja.applicantID AND i.jobAppID = japp.jobAppID)";
        
        String query6 = 
                "SELECT DISTINCT a.applicantID, a.first_name, a.last_name " + 
                "FROM JobApplicant a " +
                "JOIN JobApplication ja ON a.applicantID = ja.applicantID " + 
                    "JOIN Job j ON ja.jobID = j.jobID " +
                    "JOIN Company c ON j.companyID = c.companyID " +
                "WHERE c.name = 'Apple Canada' " +
                "UNION " +
                "SELECT DISTINCT a.applicantID, a.first_name, a.last_name " +
                "FROM JobApplicant a " +
                "JOIN JobApplication ja ON a.applicantID = ja.applicantID " +
                    "JOIN Job j ON ja.jobID = j.jobID " +
                    "JOIN Company c ON j.companyID = c.companyID " +
                "WHERE c.name = 'AMD' ";
        
        String query7 =
                "SELECT c.name AS CompanyName, COUNT(ja.jobAppID) AS TotalJobApplications " +
                "FROM Company c " +
                "JOIN Job j ON c.companyID = j.companyID " +
                "JOIN JobApplication ja ON j.jobID = ja.jobID " +
                "GROUP BY c.name " +
                "HAVING COUNT(ja.jobAppID) > ( " +
                "    SELECT AVG(job_app_count) " +
                "    FROM ( " +
                "        SELECT COUNT(ja2.jobAppID) AS job_app_count " +
                "        From JobApplication ja2 " +
                "        JOIN Job j2 ON ja2.jobID = j2.jobID " +
                "        GROUP BY j2.companyID " +
                "    ) " +
                ") " + 
                "ORDER BY TotalJobApplications DESC";
        
        String query8 =
                "SELECT DISTINCT c.name AS CompanyName " + 
                "FROM Company c " +
                "WHERE EXISTS ( " +
                "    SELECT 1 " +
                "    FROM Job j " +
                "    JOIN JobApplication ja ON j.jobID = ja.jobID " +
                "    WHERE j.companyID = c.companyID " +
                ")";
        
        String query9 =
                "SELECT DISTINCT a.applicantID, a.first_name, a.last_name " + 
                "FROM JobApplicant a " + 
                "JOIN JobApplication ja ON a.applicantID = ja.applicantID " +
                "MINUS " + 
                "SELECT DISTINCT a.applicantID, a.first_name, a.last_name " + 
                "FROM JobApplicant a " + 
                "JOIN JobApplication ja ON a.applicantID = ja.applicantID " +
                "JOIN Interview i ON ja.jobAppID = i.jobAppID";

        String query10 =
                "SELECT " + 
                "   c.name AS CompanyName, " +
                "   AVG(j.salary) AS AverageSalary, " +
                "   MIN(j.salary) AS MinimumSalary, " +
                "   MAX(j.salary) AS MaximumSalary " +
                "FROM Company c " +
                "JOIN Job j ON c.companyID = j.companyID " +
                "GROUP BY c.name " +
                "ORDER BY AverageSalary DESC";
                
        //Attaching buttons to the queries
        attachQueryButton(q1, query1);
        attachQueryButton(q2, query2);
        attachQueryButton(q3, query3);
        attachQueryButton(q4, query4);
        attachQueryButton(q5, query5);
        attachQueryButton(q6, query6);
        attachQueryButton(q7, query7);
        attachQueryButton(q8, query8);
        attachQueryButton(q9, query9);
        attachQueryButton(q10, query10);
    }

//Function that lets the buttons be clickable and converts the query results to a table in a popup message and shows a success message if it executes without errors
//and error messages if there are errors
    private void attachQueryButton(JButton button, String query) {
        button.addActionListener(e -> {
            try {
                ResultSet rs = dbConnection.executeQuery(query);
                JTable table = new JTable(DB_GUI.buildTableModel(rs));
                JOptionPane.showMessageDialog(null, new JScrollPane(table), "Query Results", JOptionPane.INFORMATION_MESSAGE);
                JOptionPane.showMessageDialog(null, "Query executed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Error executing query:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}