import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.util.Map;
import javax.swing.*;

public class AuthFrame extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private Map<String, User> userDatabase;
    private User loggedInUser = null;
    private boolean isAuthenticated = false;

    private static final Color BACKGROUND_COLOR = new Color(180, 200, 230); 

    public AuthFrame(Map<String, User> userDB) {
        this.userDatabase = userDB;
        setTitle("Authorize yourself");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        backgroundPanel.setBackground(BACKGROUND_COLOR);
        add(backgroundPanel, BorderLayout.CENTER);

        JPanel loginPanel = createLoginPanel();
        JPanel signupPanel = createSignupPanel();
        JPanel forgotPassPanel = createForgotPassPanel(); 

        cardPanel.add(loginPanel, "Login");
        cardPanel.add(signupPanel, "Signup");
        cardPanel.add(forgotPassPanel, "ForgotPass"); 
        
        backgroundPanel.add(cardPanel, new GridBagConstraints());
        
        cardPanel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                SwingUtilities.invokeLater(() -> {
                    for (Component comp : cardPanel.getComponents()) {
                        if (comp.isVisible()) {
                            if (comp == loginPanel) {
                                JButton loginBtn = (JButton) ((AuthPanel) comp).getComponent(3); 
                                getRootPane().setDefaultButton(loginBtn);
                            } else if (comp == forgotPassPanel) {
                                JButton resetBtn = (JButton) ((AuthPanel) comp).getComponent(7); 
                                getRootPane().setDefaultButton(resetBtn);
                            } else {
                                getRootPane().setDefaultButton(null);
                            }
                            break;
                        }
                    }
                });
            }
        });
    }

    private JPanel createLoginPanel() {
        AuthPanel panel = new AuthPanel("Login") {};
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.usernameField = new JTextField(15);
        panel.addField("Username:", panel.usernameField);

        panel.passwordField = new JPasswordField(15);
        panel.addField("Password:", panel.passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(this::attemptLogin);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loginBtn);
        
        panel.add(Box.createVerticalStrut(10));
        JButton forgotBtn = new JButton("Forgot Password?");
        forgotBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.addActionListener(e -> cardLayout.show(cardPanel, "ForgotPass")); 
        panel.add(forgotBtn);

        JButton signupLinkBtn = new JButton("Don't have an account?");
        signupLinkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupLinkBtn.setBorderPainted(false);
        signupLinkBtn.setContentAreaFilled(false);
        signupLinkBtn.addActionListener(e -> cardLayout.show(cardPanel, "Signup"));
        panel.add(signupLinkBtn);
        
        SwingUtilities.invokeLater(() -> {
             if (this.isVisible() && getRootPane() != null) {
                 getRootPane().setDefaultButton(loginBtn);
             }
        });

        return panel;
    }
    
    private JPanel createSignupPanel() {
        AuthPanel panel = new AuthPanel("Sign Up") {};
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        panel.usernameField = new JTextField(15);
        panel.addField("Username:", panel.usernameField);

        JTextField nameField = new JTextField(15);
        panel.addField("Name:", nameField); 

        panel.passwordField = new JPasswordField(15);
        panel.addField("Password:", panel.passwordField);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.addActionListener(e -> attemptSignup(panel.usernameField, panel.passwordField, nameField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(signupBtn);

        panel.add(Box.createVerticalStrut(10));
        JButton loginLinkBtn = new JButton("Already have an account?");
        loginLinkBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLinkBtn.setBorderPainted(false);
        loginLinkBtn.setContentAreaFilled(false);
        loginLinkBtn.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        panel.add(loginLinkBtn);
        
        return panel;
    }
    
    private JPanel createForgotPassPanel() {
        AuthPanel panel = new AuthPanel("Forgot Password") {};
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JTextField userField = new JTextField(15);
        panel.addField("Username:", userField);
        
        JTextField emailField = new JTextField(15);
        panel.addField("Email:", emailField);
        
        JPasswordField newPassField = new JPasswordField(15);
        panel.addField("New Password:", newPassField);
        
        JPasswordField confirmPassField = new JPasswordField(15);
        panel.addField("Confirm Password:", confirmPassField);
        
        JButton resetBtn = new JButton("Reset Password");
        resetBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetBtn.addActionListener(e -> attemptPasswordReset(userField, emailField, newPassField, confirmPassField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(resetBtn);

        panel.add(Box.createVerticalStrut(10));
        JButton backToLoginBtn = new JButton("Back to Login");
        backToLoginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backToLoginBtn.setBorderPainted(false);
        backToLoginBtn.setContentAreaFilled(false);
        backToLoginBtn.addActionListener(e -> cardLayout.show(cardPanel, "Login"));
        panel.add(backToLoginBtn);
        
        return panel;
    }

    private void attemptLogin(ActionEvent e) {
        AuthPanel currentPanel = (AuthPanel)((JButton)e.getSource()).getParent();
        String user = currentPanel.usernameField.getText();
        String pass = new String(currentPanel.passwordField.getPassword());
        
        if (userDatabase.containsKey(user) && userDatabase.get(user).getPassword().equals(pass)) {
            loggedInUser = userDatabase.get(user);
            isAuthenticated = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void attemptSignup(JTextField userField, JPasswordField passField, JTextField nameField) {
        String user = userField.getText();
        String pass = new String(passField.getPassword());
        String name = nameField.getText();
        
        if (user.trim().isEmpty() || pass.trim().isEmpty() || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, Password, and Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (userDatabase.containsKey(user)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please login.", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            User newUser = new User(user, pass);
            newUser.setName(name); 
            
            userDatabase.put(user, newUser);
            TrackMySpendJava.saveUserData(userDatabase); 
            
            JOptionPane.showMessageDialog(this, "Signup successful for " + name + "! Please log in now.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            cardLayout.show(cardPanel, "Login");
            
            Component loginPanel = cardPanel.getComponent(0);
            if (loginPanel instanceof AuthPanel) {
                ((AuthPanel) loginPanel).usernameField.setText(user);
                ((AuthPanel) loginPanel).passwordField.setText("");
            }
        }
    }
    
    private void attemptPasswordReset(JTextField userField, JTextField emailField, JPasswordField newPassField, JPasswordField confirmPassField) {
        String user = userField.getText().trim();
        String email = emailField.getText().trim();
        String newPass = new String(newPassField.getPassword()).trim();
        String confirmPass = new String(confirmPassField.getPassword()).trim();
        
        if (user.isEmpty() || email.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are mandatory.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New Password and Confirm Password do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userDatabase.containsKey(user)) {
            User existingUser = userDatabase.get(user);
            
            if (existingUser.getEmail().equalsIgnoreCase(email)) {
                existingUser.setPassword(newPass);
                TrackMySpendJava.saveUserData(userDatabase);
                JOptionPane.showMessageDialog(this, "Password reset successfully! Please log in with your new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
                cardLayout.show(cardPanel, "Login");
            } else {
                JOptionPane.showMessageDialog(this, "Username and Email do not match our records.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isAuthenticated() { return isAuthenticated; }
    public User getLoggedInUser() { return loggedInUser; }
}