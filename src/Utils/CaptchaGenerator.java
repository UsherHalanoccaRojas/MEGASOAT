package Utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class CaptchaGenerator {
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final Random random = new Random();
    private String codigoActual;

    public String generarCodigo() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        codigoActual = sb.toString();
        return codigoActual;
    }

    public String getCodigoActual() { return codigoActual; }

    public boolean verificar(String input) {
        return codigoActual != null && 
               codigoActual.equalsIgnoreCase(input.trim());
    }

    public BufferedImage generarImagen(String codigo) {
        int width = 160, height = 50;
        BufferedImage img = new BufferedImage(width, height, 
                                              BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                           RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo
        g.setColor(new Color(30, 30, 48));
        g.fillRect(0, 0, width, height);

        // Líneas de ruido
        g.setStroke(new BasicStroke(1.2f));
        for (int i = 0; i < 8; i++) {
            g.setColor(new Color(
                60 + random.nextInt(60),
                60 + random.nextInt(60),
                120 + random.nextInt(80),
                180
            ));
            g.drawLine(random.nextInt(width), random.nextInt(height),
                       random.nextInt(width), random.nextInt(height));
        }

        // Puntos de ruido
        for (int i = 0; i < 60; i++) {
            g.setColor(new Color(
                80 + random.nextInt(100),
                80 + random.nextInt(100),
                150 + random.nextInt(80),
                150
            ));
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            g.fillOval(x, y, 2, 2);
        }

        // Texto del captcha
        String[] fonts = {"Arial", "Courier New", "Verdana"};
        for (int i = 0; i < codigo.length(); i++) {
            String font = fonts[random.nextInt(fonts.length)];
            int size = 22 + random.nextInt(8);
            int style = random.nextBoolean() ? Font.BOLD : Font.PLAIN;
            g.setFont(new Font(font, style, size));

            // Color entre blanco y azul claro
            g.setColor(new Color(
                180 + random.nextInt(75),
                180 + random.nextInt(75),
                255
            ));

            // Rotación leve por letra
            Graphics2D g2 = (Graphics2D) g.create();
            int x = 18 + i * 26;
            int y = 32 + random.nextInt(8) - 4;
            double angle = Math.toRadians(random.nextInt(20) - 10);
            g2.rotate(angle, x, y);
            g2.drawString(String.valueOf(codigo.charAt(i)), x, y);
            g2.dispose();
        }

        g.dispose();
        return img;
    }
}