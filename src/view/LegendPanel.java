package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LegendPanel extends JPanel {

    public LegendPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(280, 0));
        setBorder(new EmptyBorder(12, 12, 12, 12));
        setFocusable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        content.add(title("Controls"));
        content.add(Box.createVerticalStrut(8));

        content.add(section("Select solid"));
        content.add(row("Tab", "Next solid"));
        content.add(row("1 / 2 / 3 / 4", "Select specific solid"));
        content.add(Box.createVerticalStrut(10));

        content.add(section("Selected solid"));
        content.add(row("Q / E", "Rotate around Y"));
        content.add(row("← / →", "Move on X"));
        content.add(row("↑ / ↓", "Move on Z"));
        content.add(row("R / F", "Move on Y"));
        content.add(row("X / C", "Scale up / down"));
        content.add(Box.createVerticalStrut(10));

        content.add(section("Camera (view)"));
        content.add(row("W / S", "Move forward / back"));
        content.add(row("A / D", "Strafe left / right"));
        content.add(row("Mouse", "Look around (yaw/pitch)"));
        content.add(Box.createVerticalStrut(12));



        add(content, BorderLayout.NORTH);
    }

    private JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        return label;
    }

    private JLabel section(String text) {
        JLabel label = new JLabel(text);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        return label;
    }

    private JPanel row(String keys, String action) {
        JPanel rowPanel = new JPanel(new BorderLayout(8, 0));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowPanel.setOpaque(false);

        JLabel keyLabel = new JLabel(keys);
        keyLabel.setFont(keyLabel.getFont().deriveFont(Font.BOLD, 12f));

        JLabel actionLabel = new JLabel(action);
        actionLabel.setFont(actionLabel.getFont().deriveFont(12f));

        rowPanel.add(keyLabel, BorderLayout.WEST);
        rowPanel.add(actionLabel, BorderLayout.CENTER);
        return rowPanel;
    }
}
