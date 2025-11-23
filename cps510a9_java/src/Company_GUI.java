import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
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
        JButton exit = new JButton("Exit");

        JButton[] buttons = {manageJobs, manageRecruiters, exit};

        for (JButton b : buttons) {
            b.setFont(new Font("Times New Roman", Font.BOLD, 20));
            b.setBackground(LightBlue);
            buttonPanel.add(b);
        }

        add(buttonPanel, BorderLayout.CENTER);

        /* Add button functionality */
        manageJobs.addActionListener(e -> viewCompanyJobs());
        manageRecruiters.addActionListener(e -> viewRecruiters());
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

    /* Extracts a row into a map of columnName → value */
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

    /* Displays full details in a scrollable window */
    private void showDetailsPopup(String title, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();

        for (String key : data.keySet()) {
            sb.append(key).append(": ").append(data.get(key)).append("\n");
        }

        JTextArea text = new JTextArea(sb.toString());
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(text);
        scroll.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scroll, title, JOptionPane.PLAIN_MESSAGE);
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

            JOptionPane.showMessageDialog(this, panel, "Company Job Listings", JOptionPane.PLAIN_MESSAGE);

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

            JButton viewBtn = new JButton("View Selected Recruiter");
            viewBtn.addActionListener(ev -> {
                int row = table.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Select a recruiter first.");
                    return;
                }
                showDetailsPopup("Recruiter Details", extractRow(table, row));
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(table), BorderLayout.CENTER);
            panel.add(viewBtn, BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(this, panel, "Company Recruiters", JOptionPane.PLAIN_MESSAGE);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
