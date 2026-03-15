package view;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    private final Panel panel;
    private final LegendPanel legendPanel;

    public Window(int width, int heigth) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("PGRF1 2024/2025");
        setLayout(new BorderLayout());

        panel = new Panel(width, heigth);
        legendPanel = new LegendPanel();

        add(panel, BorderLayout.CENTER);
        add(legendPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        panel.setFocusable(true);
        panel.requestFocusInWindow();
    }

    public Panel getPanel() {
        return panel;
    }

    public LegendPanel getLegendPanel() {
        return legendPanel;
    }
}
