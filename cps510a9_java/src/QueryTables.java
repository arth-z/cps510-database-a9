import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class QueryTables extends JPanel {

    private final DBConnection dbConnection;

    public QueryTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("Query Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));

        JButton q1 = new JButton("Companies with Posted Jobs but No Interviews");
        JButton q2 = new JButton("Companies Above Average Working Hours");
        JButton q3 = new JButton("Recruiters & Applicants Connected to Apple Canada");
        JButton q4 = new JButton("Companies with Applications Under Review");
        JButton q5 = new JButton("Applicants Who Have Never Been Interviewed");

        JButton[] buttons = {q1, q2, q3, q4, q5};
        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 18));
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

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

        attachQueryButton(q1, query1);
        attachQueryButton(q2, query2);
        attachQueryButton(q3, query3);
        attachQueryButton(q4, query4);
        attachQueryButton(q5, query5);
    }

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