import java.awt.*;
import javax.swing.*;

public abstract class AuthPanel extends JPanel {
    protected JTextField usernameField;
    protected JPasswordField passwordField;

    protected AuthPanel(String title) {
        setBackground(new Color(255, 255, 255, 200)); 
        setPreferredSize(new Dimension(300, 300));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));
    }

    protected void addField(String labelText, JComponent field) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(90, 25));
        
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        fieldPanel.setMaximumSize(new Dimension(350, 40));
        add(fieldPanel);
        add(Box.createVerticalStrut(10));
    }
}