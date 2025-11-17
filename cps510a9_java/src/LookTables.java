import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

/* This class allows user to view (read) records from any table in the application. */
public class LookTables extends JPanel {

    private final DBConnection dbConnection;  /* Manages database connection to Oracle */
    private final JTextArea outputArea = new JTextArea(); /* Displays executed SQL statements and results */
    private final JComboBox<String> tableSelector; /* Dropdown for selecting which table to view */
    private final JTable resultTable; /* Displays the data retrieved from the database */

    /* Sets upp all UI components, such as title, dropdown menu, result table, and output area */
    public LookTables (DB_GUI gui, String username, String password) throws SQLException {
       
        /* Get database connection instance using credentials */
        this.dbConnection = DBConnection.getInstance(username, password);
        setLayout(new BorderLayout());

        /* Add a title label at the top */
        JLabel title = new JLabel("Look Tables", SwingConstants.CENTER);
        title.setFont(new Font("Times New Roman", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        /* Control Panel (Top Section): Table Selector and Look Button */
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Select a Table to View"));

        /* Dropdown containing available tables in the schema */
        tableSelector = new JComboBox<>(new String[]{
            "Company", "Recruiter", "Job", "JobApplicant", "JobApplication", "Resume", "Interview"
        });

        /* Button to execute query for the selected table */
        JButton lookButton = new JButton("Look at Table");
        lookButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        lookButton.addActionListener(e -> lookTable());

        /* Add components to the control panel */
        controlPanel.add(new JLabel("Table:"));
        controlPanel.add(tableSelector);
        controlPanel.add(lookButton);

        add(controlPanel, BorderLayout.NORTH);

        /* Center Section: Table Data Display */
        resultTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(resultTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Table Data"));
        add(tableScroll, BorderLayout.CENTER);

        /* SQL Output Log (Bottom Section) */
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setBorder(BorderFactory.createTitledBorder("SQL Output"));
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setPreferredSize(new Dimension(600, 150));
        add(outputScroll, BorderLayout.SOUTH);

        /* Bottom Buttons: View Output + Back */
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        /* Button to open the SQL log in a separate output window */
        JButton viewOutputButton = new JButton("View Output");
        viewOutputButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        viewOutputButton.addActionListener(e -> gui.addOutputPanel("ViewTablesOutput", outputArea));

        /* Button to navigate back to the main menu */
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 18));
        backButton.addActionListener(e -> gui.showMainMenu());

        /* Add both buttons to bottom panel */
        bottomPanel.add(viewOutputButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.PAGE_END);
    }

    /* Called when the user clicks the "Look at Table" button. Retrieves the selected table name, builds a "SELECT * FROM <table>" query, logs it in the output area, and executes it for display */
    private void lookTable() {
        String table = (String) tableSelector.getSelectedItem();
        String sql = "SELECT * FROM " + table;
        outputArea.setText("");
        log("Executing query: " + sql);
        executeAndDisplay(sql);
    }

    /* Executes the provided SQL query and displays the result set in a JTable. Also logs success messages or SQL errors in the output area */
    private void executeAndDisplay(String sql) {
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            /* Convert ResultSet to a table model and display it */
            DefaultTableModel model = buildTableModel(rs);
            resultTable.setModel(model);

            /* Log the number of rows retrieved */
            log("Query executed successfully. Rows retrieved: " + model.getRowCount());

        /* Handle SQL errors gracefully */
        } catch (SQLException ex) {
            log("SQL Error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error executing query:\n" + ex.getMessage(),
                    "SQL Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Converts a ResultSet object into a DefaultTableModel for displaying in a JTable. Dynamically retrieves column names and row data from the query result */
    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        /* Retrieve column names from metadata */
        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(meta.getColumnName(i));
        }

        /* Retrieve all rows from the ResultSet */
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        /* Return populated table model */
        return new DefaultTableModel(data, columnNames);
    }

    /* Helper to append text to output area */
    private void log(String message) {
        outputArea.append(message + "\n");
    }
}


