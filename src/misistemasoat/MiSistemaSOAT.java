package misistemasoat;

import Utils.UITheme;
import View.Panels.LoginFrame;
import javax.swing.SwingUtilities;

public class MiSistemaSOAT {
    public static void main(String[] args) {
        UITheme.applyGlobalDefaults();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}