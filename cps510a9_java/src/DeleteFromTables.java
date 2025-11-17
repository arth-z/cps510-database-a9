import javax.swing.*;
import java.awt.*;
import java.sql.*; 

public class DeleteFromTables extends JPanel {

    private final DBConnection dbConnection;

    public DeleteFromTables(DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password); // connect to the DB

        // set up the GUI
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Delete From Tables", SwingConstants.CENTER);
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

        // attach the *same* functionality to each button, just different table and query
        attachButton(t1, query1, "company");
        attachButton(t2, query2, "recruiter");
        attachButton(t3, query3, "job");
        attachButton(t4, query4, "jobapplicant");
        attachButton(t5, query5, "jobapplication");
        attachButton(t6, query6, "resume");
        attachButton(t7, query7, "interview");
    }

    // thankfully the process for deletion based on primary key be generalised pretty easily because we named all our primary keys really easy names
    // ...except for job applicant and application, which we made exceptions for in the code below
    public void attachButton(JButton button, String query, String tableName) {
        button.addActionListener(e -> {
            try {
                String idColumn = tableName.equals("jobapplicant") ? "applicantID" : tableName + "ID"; // if jobapplicant, use applicantID instead
                idColumn = tableName.equals("jobapplication") ? "jobAppID" : idColumn; // if jobapplication, use jobAppID instead
                
                // then literally just ask for the primary key value to delete
                // since the rest of our tables are literally just named tableName+"ID", this works for all other tables
                String idValue = JOptionPane.showInputDialog("Enter " + idColumn + " of the row to delete from " + tableName + ":");
                if (idValue != null && !idValue.trim().isEmpty()) {
                    // prepare a statement
                    String deleteSQL = "DELETE FROM " + tableName + " WHERE " + idColumn + " = ?";
                    PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(deleteSQL);
                    pstmt.setInt(1, Integer.parseInt(idValue)); // set the primary key value
                    int rowsAffected = pstmt.executeUpdate(); // execute
                    if (rowsAffected > 0) { // if something was deleted
                        JOptionPane.showMessageDialog(null, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                        // show the updated table
                        ResultSet rs = dbConnection.executeQuery(query);
                        JTable table = new JTable(DB_GUI.buildTableModel(rs));
                        JOptionPane.showMessageDialog(null, new JScrollPane(table), "Updated Table", JOptionPane.INFORMATION_MESSAGE);

                    } else { // else something went wrong
                        JOptionPane.showMessageDialog(null, "No record found with the given ID.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) { // catch any SQL errors
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error deleting record: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) { // catch any other errors
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error processing query:\n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
  
}