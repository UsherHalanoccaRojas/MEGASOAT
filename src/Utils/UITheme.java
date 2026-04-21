package Utils;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.ColorUIResource;

public class UITheme {

    // ── Paleta dark ─────────────────────────────────────────────
    public static final Color BG_PRIMARY     = new Color(15, 15, 25);
    public static final Color BG_SECONDARY   = new Color(22, 22, 36);
    public static final Color BG_CARD        = new Color(30, 30, 48);
    public static final Color BG_HOVER       = new Color(42, 42, 65);
    public static final Color ACCENT_BLUE    = new Color(29, 175, 206);  // Turquesa principal
    public static final Color ACCENT_HOVER   = new Color(45, 190, 220);  
    public static final Color ACCENT_GREEN   = new Color(34, 197, 94);
    public static final Color ACCENT_RED     = new Color(239, 68, 68);
    public static final Color ACCENT_YELLOW  = new Color(234, 179, 8);
    public static final Color TEXT_PRIMARY   = new Color(240, 240, 255);
    public static final Color TEXT_SECONDARY = new Color(130, 130, 165);
    public static final Color BORDER_COLOR   = new Color(50, 50, 75);

    // ── Fuentes ──────────────────────────────────────────────────
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL  = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * Llama esto en main() ANTES de crear cualquier ventana.
     * Fuerza colores globales del L&F para que no sobreescriba nuestro tema.
     */
    public static void applyGlobalDefaults() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",          new ColorUIResource(BG_PRIMARY));
        UIManager.put("ScrollPane.background",     new ColorUIResource(BG_SECONDARY));
        UIManager.put("Viewport.background",       new ColorUIResource(BG_CARD));
        UIManager.put("Table.background",          new ColorUIResource(BG_CARD));
        UIManager.put("Table.foreground",          new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Table.gridColor",           new ColorUIResource(BORDER_COLOR));
        UIManager.put("TableHeader.background",    new ColorUIResource(BG_SECONDARY));
        UIManager.put("TableHeader.foreground",    new ColorUIResource(TEXT_SECONDARY));
        UIManager.put("List.background",           new ColorUIResource(BG_SECONDARY));
        UIManager.put("List.foreground",           new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("List.selectionBackground",  new ColorUIResource(ACCENT_BLUE));
        UIManager.put("List.selectionForeground",  new ColorUIResource(Color.WHITE));
        UIManager.put("ComboBox.background",       new ColorUIResource(BG_HOVER));
        UIManager.put("ComboBox.foreground",       new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("ComboBox.selectionBackground", new ColorUIResource(ACCENT_BLUE));
        UIManager.put("ComboBox.selectionForeground", new ColorUIResource(Color.WHITE));
        UIManager.put("TextField.background",      new ColorUIResource(BG_HOVER));
        UIManager.put("TextField.foreground",      new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("TextField.caretForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("TextArea.background",       new ColorUIResource(BG_HOVER));
        UIManager.put("TextArea.foreground",       new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("TextArea.caretForeground",  new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("CheckBox.background",       new ColorUIResource(BG_CARD));
        UIManager.put("CheckBox.foreground",       new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Label.foreground",          new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("SplitPane.background",      new ColorUIResource(BG_PRIMARY));
        UIManager.put("SplitPane.dividerSize",     6);
        UIManager.put("TabbedPane.background",     new ColorUIResource(BG_SECONDARY));
        UIManager.put("TabbedPane.foreground",     new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("TabbedPane.selected",       new ColorUIResource(BG_CARD));
        UIManager.put("OptionPane.background",     new ColorUIResource(BG_CARD));
        UIManager.put("OptionPane.messageForeground", new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Button.background",         new ColorUIResource(BG_HOVER));
        UIManager.put("Button.foreground",         new ColorUIResource(TEXT_PRIMARY));
        UIManager.put("Button.select",             new ColorUIResource(ACCENT_BLUE));
        UIManager.put("ScrollBar.background",      new ColorUIResource(BG_SECONDARY));
        UIManager.put("ScrollBar.thumb",           new ColorUIResource(BORDER_COLOR));
        UIManager.put("ScrollBar.thumbHighlight",  new ColorUIResource(BG_HOVER));
        UIManager.put("ScrollBar.track",           new ColorUIResource(BG_SECONDARY));
    }

    // ── Botones ──────────────────────────────────────────────────

    public static JButton createPrimaryButton(String text) {
        return buildButton(text, ACCENT_BLUE, ACCENT_HOVER, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return buildButton(text, ACCENT_RED, new Color(255, 90, 90), Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        return buildButton(text, BG_HOVER, BORDER_COLOR, TEXT_PRIMARY);
    }

    private static JButton buildButton(String text, Color bg, Color hover, Color fg) {
        RoundedButton btn = new RoundedButton(text, 22, bg, hover, fg);
        btn.setFont(FONT_BUTTON);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 38));
        return btn;
    }

    // ── Campos ───────────────────────────────────────────────────

    public static JTextField createTextField(String hint) {
        JTextField f = new JTextField();
        f.setFont(FONT_BODY);
        f.setForeground(TEXT_PRIMARY);
        f.setBackground(BG_HOVER);
        f.setCaretColor(TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10, BORDER_COLOR),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        f.setPreferredSize(new Dimension(200, 36));
        return f;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> c = new JComboBox<>(items);
        c.setFont(FONT_BODY);
        c.setForeground(TEXT_PRIMARY);
        c.setBackground(BG_HOVER);
        c.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? ACCENT_BLUE : BG_HOVER);
                setForeground(isSelected ? Color.WHITE : TEXT_PRIMARY);
                setFont(FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                return this;
            }
        });
        c.setPreferredSize(new Dimension(200, 36));
        return c;
    }

    // ── Cards y contenedores ─────────────────────────────────────

    public static JPanel createCard(String title) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(14, BORDER_COLOR),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        if (title != null && !title.isEmpty()) {
            JLabel lbl = new JLabel(title);
            lbl.setFont(FONT_HEADER);
            lbl.setForeground(TEXT_PRIMARY);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            card.add(lbl, BorderLayout.NORTH);
        }
        return card;
    }

    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        sp.getViewport().setBackground(BG_CARD);
        sp.setBackground(BG_SECONDARY);
        sp.getVerticalScrollBar().setBackground(BG_SECONDARY);
        sp.getHorizontalScrollBar().setBackground(BG_SECONDARY);
        return sp;
    }

    // ── Tabla ────────────────────────────────────────────────────

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(BG_CARD);
        table.setSelectionBackground(ACCENT_BLUE);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setRowHeight(34);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(10, 0));
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setForeground(TEXT_SECONDARY);
        table.getTableHeader().setBackground(BG_SECONDARY);
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR)
        );
        // Filas alternadas
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (sel) {
                    setBackground(ACCENT_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(row % 2 == 0 ? BG_CARD : BG_HOVER);
                    setForeground(TEXT_PRIMARY);
                }
                setFont(FONT_BODY);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
    }

    // ── Clases internas ──────────────────────────────────────────

    public static class RoundedButton extends JButton {
        private final int radius;
        private final Color bgNormal;
        private final Color bgHover;
        private Color current;

        public RoundedButton(String text, int radius, Color bg, Color hover, Color fg) {
            super(text);
            this.radius   = radius;
            this.bgNormal = bg;
            this.bgHover  = hover;
            this.current  = bg;
            setForeground(fg);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    current = bgHover; repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    current = bgNormal; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(current);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class RoundedBorder extends AbstractBorder {
        private final int radius;
        private final Color color;
        public RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color  = color;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) { return new Insets(4, 8, 4, 8); }
    }
    
}