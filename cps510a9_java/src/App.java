/* Main entry point for the application. Launches the GUI. */
public class App {
    public static void main(String[] args) {
        
        /* Launch the GUI safely on the Event Dispatch Thread. This prevents potential threading issues with Swing components */
        javax.swing.SwingUtilities.invokeLater(() -> {
            /* Create an instance of the main graphical interface (DB_GUI) */
            DB_GUI gui = new DB_GUI();
            /* Make the GUI visible to the user */
            gui.setVisible(true);
        });
    }
}
