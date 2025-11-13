public class App {
    public static void main(String[] args) {
        
        javax.swing.SwingUtilities.invokeLater(() -> {
            DB_GUI gui = new DB_GUI();
            gui.setVisible(true);
        });
    }
}
