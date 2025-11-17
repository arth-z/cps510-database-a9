import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class LookTables extends JPanel {

    private final DBConnection dbConnection;
    private final JTextArea outputArea = new JTextArea();
    private final JComboBox<String> tableSelector;
    private final JTable resultTable;

    public LookTables (DB_GUI gui, String username, String password) throws SQLException {
        this.dbConnection = DBConnection.getInstance(username, password);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Look Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Select a Table to View"));

        tableSelector = new JComboBox<>(new String[]{
            "Company", "Recruiter", "Job", "JobApplicant", "JobApplication", "Resume", "Interview"
        });

        JButton lookButton = new JButton("Look at Table");
        lookButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        lookButton.addActionListener(e -> lookTable());

        controlPanel.add(new JLabel("Table:"));
        controlPanel.add(tableSelector);
        controlPanel.add(lookButton);

        add(controlPanel, BorderLayout.NORTH);

        resultTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Table Data"));
        add(tableScroll, BorderLayout.CENTER);

        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBorder(BorderFactory.createTitledBorder("SQL Output"));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setPreferredSize(new Dimension(600, 150));
        add(outputScroll, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton viewOutputButton = new JButton("View Output");
        viewOutputButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        viewOutputButton.addActionListener(e -> gui.addOutputPanel("ViewTablesOutput", outputArea));

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        backButton.addActionListener(e -> gui.showMainMenu());

        bottomPanel.add(viewOutputButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    private void lookTable() {
        String table = (String) tableSelector.getSelectedItem();
        String sql = "SELECT * FROM " + table;
        outputArea.setText("");
        log("Executing query: " + sql);
        executeAndDisplay(sql);
    }

    private void executeAndDisplay(String sql) {
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            DefaultTableModel model = buildTableModel(rs);
            resultTable.setModel(model);
            log("Query executed successfully. Rows retrieved: " + model.getRowCount());

        } catch (SQLException ex) {
            log("SQL Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error executing query:\n" + ex.getMessage(),
                    "SQL Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(meta.getColumnName(i));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }

    private void log(String message) {
        outputArea.append(message + "\n");
    }
}


