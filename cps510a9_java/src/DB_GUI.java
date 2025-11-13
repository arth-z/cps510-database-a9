import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;  

public class DB_GUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public DB_GUI() {
       setTitle("Online Job Bank System");
       setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       setSize(800, 600);
       setLocationRelativeTo(null);

       cardLayout = new CardLayout();
       mainPanel = new JPanel(cardLayout);

       mainPanel.add(new Login(this), "login");

       JPanel mainMenu = new JPanel();
       mainMenu.add(new JLabel("Main Menu"));
       mainPanel.add(mainMenu, "mainmenu");
       


       add(mainPanel); 
       cardLayout.show(mainPanel, "login");
    }

    public void showMainMenu() {
        cardLayout.show(mainPanel, "mainmenu");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DB_GUI gui = new DB_GUI();
            gui.setVisible(true);
        });
    }
    
}
