import javax.swing.SwingUtilities;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            MainController controller = new MainController();
            MainView view = new MainView(controller);
            controller.setView(view);
            view.init();
        });
    }

}
