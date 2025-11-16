import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;  

public class DB_GUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private String username;
    private String password;

    public DB_GUI() {
        setTitle("Online Job Bank System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new Login(this), "login");

        JPanel mainMenu = createMainMenu();
        mainPanel.add(mainMenu, "mainmenu");
        mainPanel.setBackground(Color.BLUE);
            
        add(mainPanel); 
        cardLayout.show(mainPanel, "login");
    }

    private JPanel createMainMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("ONLINE JOB BANK SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        menuPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        Color LightBlue = new Color(173, 216, 230);

        JButton createButton = new JButton("Create Tables");
        JButton dropButton = new JButton("Drop Tables");
        JButton populateButton = new JButton("Populate Tables");
        JButton viewButton = new JButton("View Tables");
        JButton queryButton = new JButton("Query Tables");
        JButton SQLButton = new JButton("Custom SQL");
        JButton exitButton = new JButton("Exit");

        JButton[] buttons = {createButton, dropButton, populateButton, viewButton, queryButton, SQLButton, exitButton};

        for (JButton button : buttons) {
            button.setFont(new Font("Times New Roman", Font.BOLD, 20));
            button.setBackground(LightBlue);
            buttonPanel.add(button);
        }

        // Add action listeners for navigation
        createButton.addActionListener(e -> showCreateTables());
        dropButton.addActionListener(e -> showDropTables());
        populateButton.addActionListener(e -> showPopulateTables());
        // viewButton.addActionListener(e -> cardLayout.show(mainPanel, "View Tables"));
        queryButton.addActionListener(e -> showQueryTables());
        // SQLButton.addActionListener(e -> cardLayout.show(mainPanel, "Custom SQL"));
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        return menuPanel; 

    }

    public static void executeButtonActionEvent(JButton tableButton, DBConnection databaseConnection, String query) {
        tableButton.addActionListener(actionEvent -> {
            try {
                ResultSet queryResult = databaseConnection.executeQuery(query);
                JTable queryResultTable = new JTable(buildTableModel(queryResult));
                JOptionPane.showMessageDialog(null, new JScrollPane(queryResultTable));
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static DefaultTableModel buildTableModel(ResultSet queryResult) throws SQLException {

        ResultSetMetaData queryMetaData = queryResult.getMetaData();
        int columnCount = queryMetaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> queryDataVector = new Vector<>();

        for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {
            columnNames.add(queryMetaData.getColumnName(columnNumber));
        }

        while (queryResult.next()) {
            Vector<Object> tempDataVector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                tempDataVector.add(queryResult.getObject(columnIndex));
            }
            queryDataVector.add(tempDataVector);
        }
        return new DefaultTableModel(queryDataVector, columnNames);
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "mainmenu");
    }
    public void showCreateTables() {
        try {
            CreateTables createTablesPanel = new CreateTables(this, username, password);
            mainPanel.add(createTablesPanel, "CreateTables");
            cardLayout.show(mainPanel, "CreateTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading CreateTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDropTables() {
        try {
            DropTables dropTablesPanel = new DropTables(this, username, password);
            mainPanel.add(dropTablesPanel, "DropTables");
            cardLayout.show(mainPanel, "DropTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading DropTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showPopulateTables() {
        try {
            PopulateTables populateTablesPanel = new PopulateTables(this, username, password);
            mainPanel.add(populateTablesPanel, "PopulateTables");
            cardLayout.show(mainPanel, "PopulateTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading PopulateTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showQueryTables() {
    try {
        QueryTables queryTablesPanel = new QueryTables(this, username, password);
        mainPanel.add(queryTablesPanel, "QueryTables");
        cardLayout.show(mainPanel, "QueryTables");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this,
            "Error loading QueryTables panel: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addOutputPanel(String name, JTextArea outputArea) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Times New Roman", Font.BOLD, 20));
        backButton.addActionListener(ev -> showMainMenu());
        panel.add(backButton, BorderLayout.SOUTH);

        mainPanel.add(panel, name);
        cardLayout.show(mainPanel, name);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DB_GUI gui = new DB_GUI();
            gui.setVisible(true);
        });
    }
    
}
