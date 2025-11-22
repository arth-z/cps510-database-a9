import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;  

/* This class represents the main GUI for the application */
public class DB_GUI extends JFrame {
    private CardLayout cardLayout;   /* Manages switching between different panels */
    private JPanel mainPanel;        /* Container for all screens (login, main menu, etc.) */
    private String username;         /* Stores Oracle username for database access */
    private String password;         /* Stores Oracle password for database access */
    private String userRole;         /* Stores selected user role */
    private int applicantID;         /* Stores applicantID */
    private int recruiterID;         /* Stores recruiterID */
    private int companyID;           /* Stores companyID */

    /* Sets up the frame, layout, and adds the login and main menu panels */
    public DB_GUI() {
        setTitle("Online Job Bank System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        /* Add the Login screen as the first panel */
        mainPanel.add(new Login(this), "login");

        /* Add the Main Menu screen */
        JPanel mainMenu = createMainMenu();
        mainPanel.add(mainMenu, "mainmenu");
        mainPanel.setBackground(Color.BLUE);
        
        /* Add the main panel to the frame and show login screen first */
        add(mainPanel); 
        cardLayout.show(mainPanel, "login");
    }

    /* Creates the main menu panel. Each button navigates to a different feature of the system */
    private JPanel createMainMenu() {
        JPanel menuPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("ONLINE JOB BANK SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        menuPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        Color LightBlue = new Color(173, 216, 230);

        /* Define main menu buttons for all database operations */
        JButton createButton = new JButton("Create Tables");
        JButton dropButton = new JButton("Drop Tables");
        JButton populateButton = new JButton("Populate Tables");
        JButton viewButton = new JButton("View Tables");
        JButton queryButton = new JButton("Query Tables");
        JButton updateButton = new JButton("Update Tables");
        JButton addButton = new JButton("Add to Tables");
        JButton deleteButton = new JButton("Delete from Tables");
        JButton lookButton = new JButton("Look at Tables");
        JButton exitButton = new JButton("Exit");

        /* Group buttons into an array for consistent styling and layout */
        JButton[] buttons = {createButton, dropButton, populateButton, viewButton, queryButton, updateButton, addButton, deleteButton, lookButton, exitButton};

        /* Style all buttons */
        for (JButton button : buttons) {
            button.setFont(new Font("Times New Roman", Font.BOLD, 20));
            button.setBackground(LightBlue);
            buttonPanel.add(button);
        }

        /* Add action listeners for navigation to other panels */
        createButton.addActionListener(e -> showCreateTables());
        dropButton.addActionListener(e -> showDropTables());
        populateButton.addActionListener(e -> showPopulateTables()); 
        queryButton.addActionListener(e -> showQueryTables());
        viewButton.addActionListener(e -> showViewTables());
        updateButton.addActionListener(e -> showUpdateTables());
        addButton.addActionListener(e -> showAddToTables());
        deleteButton.addActionListener(e -> showDeleteFromTables());
        lookButton.addActionListener(e -> showLookTables());
        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(buttonPanel, BorderLayout.CENTER);
        return menuPanel; 

    }

    /* Utility method to attach a query button to an SQL statement. Executes a query when the button is clicked and displays the result in a JTable. */
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

    /* Converts a ResultSet (from a SQL query) into a DefaultTableModel. This allows the data to be displayed inside a JTable. */
    public static DefaultTableModel buildTableModel(ResultSet queryResult) throws SQLException {
        ResultSetMetaData queryMetaData = queryResult.getMetaData();
        int columnCount = queryMetaData.getColumnCount();
        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> queryDataVector = new Vector<>();

        /* Extract column names */
        for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {
            columnNames.add(queryMetaData.getColumnName(columnNumber));
        }

        /* Extract each row of data */
        while (queryResult.next()) {
            Vector<Object> tempDataVector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                tempDataVector.add(queryResult.getObject(columnIndex));
            }
            queryDataVector.add(tempDataVector);
        }
        /* Return a model suitable for JTable display */
        return new DefaultTableModel(queryDataVector, columnNames);
    }

    /* Displays the main menu screen */
    public void showMainMenu() {
        cardLayout.show(mainPanel, "mainmenu");
    }

     /* Navigation methods: each loads the respective panel dynamically */
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

    public void showViewTables() {
        try {
            ViewTables viewTablesPanel = new ViewTables(this, username, password);
            mainPanel.add(viewTablesPanel, "ViewTables");
            cardLayout.show(mainPanel, "ViewTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading ViewTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showUpdateTables() {
        try {
            UpdateTables updateTablesPanel = new UpdateTables(this, username, password);
            mainPanel.add(updateTablesPanel, "UpdateTables");
            cardLayout.show(mainPanel, "UpdateTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading UpdateTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showAddToTables() {
        try {
            AddToTables updateTablesPanel = new AddToTables(this, username, password);
            mainPanel.add(updateTablesPanel, "AddToTables");
            cardLayout.show(mainPanel, "AddToTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading AddToTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDeleteFromTables() {
        try {
            DeleteFromTables updateTablesPanel = new DeleteFromTables(this, username, password);
            mainPanel.add(updateTablesPanel, "DeleteFromTables");
            cardLayout.show(mainPanel, "DeleteFromTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading DeleteFromTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showLookTables() {
        try {
            LookTables lookTablesPanel = new LookTables(this, username, password);
            mainPanel.add(lookTablesPanel, "ViewTables");
            cardLayout.show(mainPanel, "ViewTables");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading ViewTables panel: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /* Added menus for the different roles in the application (Applicant, Recruiter, Company) */
    public void showApplicantGUI() throws SQLException {
        JPanel applicantPanel = new Applicant_GUI(this, username, password);
        mainPanel.add(applicantPanel, "ApplicantUI");
        cardLayout.show(mainPanel, "ApplicantUI");
    }

    public void showRecruiterGUI() throws SQLException {
        JPanel recruiterPanel = new Recruiter_GUI(this, username, password);
        mainPanel.add(recruiterPanel, "RecruiterUI");
        cardLayout.show(mainPanel, "RecruiterUI");
    }

    public void showCompanyGUI() throws SQLException {
        JPanel companyPanel = new Company_GUI(this, username, password);
        mainPanel.add(companyPanel, "CompanyUI");
        cardLayout.show(mainPanel, "CompanyUI");
    }


    /* Stores the Oracle login credentials (used for all DB operations) */
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUserRole(String role) {
        this.userRole = role;
    }

    /* Getters/Setters for applicant, recruiter, and company IDs */
    public void setApplicantID(int id) {
        this.applicantID = id;
    }

    public int getApplicantID() {
        return applicantID;
    }

    public void setRecruiterID(int id) {
        this.recruiterID = id;
    }

    public int getRecruiterID() {
        return recruiterID;
    }

    public void setCompanyID(int id) {
        this.companyID = id;
    }

    public int getCompanyID() {
        return companyID;
    }


    /* Adds a new panel to display output (e.g., logs or SQL results) with a back button */
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

    /* Shows a small loading popup while DB initializes */
    private void showLoadingAndInitialize(Runnable initTask, Runnable onDone) {

        JDialog loading = new JDialog(this, "Loading", true);
        loading.setLayout(new BorderLayout());
        loading.setSize(300, 120);
        loading.setLocationRelativeTo(this);
        loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        JLabel msg = new JLabel("Loading data... please wait", SwingConstants.CENTER);
        msg.setFont(new Font("Times New Roman", Font.PLAIN, 16));
        loading.add(msg, BorderLayout.NORTH);

        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        loading.add(bar, BorderLayout.CENTER);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                initTask.run();
                return null;
            }

            @Override
            protected void done() {
                loading.dispose();
                onDone.run();
            }
        };

        worker.execute();
        loading.setVisible(true);
    }

    /* Handles initialization after login with loading popup */
    public void initializeAfterLogin() {

        if (userRole.equals("Database Admin")) {
            showMainMenu();
            return;
        }

        showLoadingAndInitialize(
            () -> {
                try {
                    DatabaseInitializer init = new DatabaseInitializer(username, password);
                    init.initialize();
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                            "Initialization error:\n" + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE)
                    );
                }
            },
            () -> {
                /* Navigation after loading finishes */
                switch (userRole) {
                    case "Applicant":  try {
                            showApplicantGUI();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } break;
                    case "Recruiter":  try{
                        showRecruiterGUI(); 
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } break;
                    case "Company":    try {
                            showCompanyGUI();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } break;
                }
            }
        );
    }

    /* Main method to launch the GUI application */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DB_GUI gui = new DB_GUI();
            gui.setVisible(true);
        });
    }
    
}
