package Utils;

import javax.swing.*;
import java.awt.*;

public class AppIcon {
    private static final ImageIcon ICON = new ImageIcon(AppIcon.class.getResource("/resources/logo.jpeg"));

    public static void aplicarIcono(JFrame frame) {
        frame.setIconImage(ICON.getImage());
    }

    public static JLabel crearLogoLabel() {
        JLabel lbl = new JLabel(ICON);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }
}
